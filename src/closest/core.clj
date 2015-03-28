(ns closest.core
  (:import (org.apache.lucene.store RAMDirectory NIOFSDirectory Directory)
           (org.apache.lucene.document TextField Document Field StringField SortedDocValuesField SortedNumericDocValuesField Field$Store IntField NumericDocValuesField)
           (org.apache.lucene.index IndexWriter IndexWriterConfig DirectoryReader IndexReader)
           (org.apache.lucene.analysis.standard StandardAnalyzer)
           (org.apache.lucene.search IndexSearcher ScoreDoc Sort SortField SortField$Type)
           (org.apache.lucene.queryparser.classic QueryParser)
           (java.nio.file Paths)
           (org.apache.lucene.util BytesRef NumericUtils)))

(def analyzer (StandardAnalyzer.))

(defn memory-index
  "Create a new index in RAM."
  []
  (RAMDirectory.))

(defn disk-index
  "Create a new index in a directory on disk."
  [^String dir-path]
  (NIOFSDirectory. (Paths/get dir-path (into-array String []) )))

(defn- index-writer
  "Create an IndexWriter."
  ^IndexWriter
  [index]
  (IndexWriter. index
                (IndexWriterConfig. analyzer)))

(defn- index-reader
  "Create an IndexReader."
  ^IndexReader
  [index]
  (DirectoryReader/open ^Directory index))

(defn text-field [field-name stored]
  "A field that is indexed and tokenized, without term vectors. For example this would be used on a 'body' field, that contains the bulk of a document's text."
  {field-name (if stored TextField/TYPE_STORED TextField/TYPE_NOT_STORED)})

(defn string-field [field-name stored]
  "A field that is indexed but not tokenized: the entire String value is indexed as a single token. For example this might be used for a 'country' field or an 'id' field, or any field that you intend to use for sorting or access through the field cache."
  {field-name (if stored StringField/TYPE_STORED StringField/TYPE_NOT_STORED)})

(defn sorted-doc-values-field [field-name]
  "Field that stores a per-document BytesRef value, indexed for sorting."
  {field-name SortedDocValuesField/TYPE})

(defn sorted-numeric-doc-values-field [field-name]
  "Field that stores a per-document long value for scoring, sorting or value retrieval."
  {field-name NumericDocValuesField/TYPE})

(defn int-field [field-name stored]
  "Field that indexes int values for efficient range filtering and sorting"
  {field-name (if stored StringField/TYPE_STORED StringField/TYPE_NOT_STORED)})

(defn string-sort [field-name reverse]
  {:name field-name :type SortField$Type/STRING :reverse reverse})

(defn int-sort [field-name reverse]
  {:name field-name :type SortField$Type/INT :reverse reverse})

(defn str->lng [value]
  (try
    (if (nil? value)
      0
      (.longValue (Integer/valueOf value)))
    (catch Exception e 0)))

(defn- map->doc [map document-field-options]
  "Create a Lucene Document from a map of values and a map of field options"
  (let [document (Document.)]
    (doseq [[key value] map]
      (let [type (key document-field-options)
            field-name (name key)]
        (if (nil? type)
          (throw (Exception. (str "Missing document field options for " (name key)) ))
          (.add document
                (cond
                  (identical? type SortedDocValuesField/TYPE) (SortedDocValuesField. field-name (BytesRef. value))
                  (identical? type NumericDocValuesField/TYPE) (NumericDocValuesField. field-name (str->lng value))
                  (identical? type TextField/TYPE_STORED) (TextField. field-name value Field$Store/YES)
                  (identical? type TextField/TYPE_NOT_STORED) (TextField. field-name value Field$Store/NO)
                  (identical? type StringField/TYPE_STORED) (StringField. field-name value Field$Store/YES)
                  (identical? type StringField/TYPE_NOT_STORED) (StringField. field-name value Field$Store/NO)
                  (identical? type IntField/TYPE_NOT_STORED) (IntField. field-name value Field$Store/YES)
                  (identical? type IntField/TYPE_NOT_STORED) (IntField. field-name value Field$Store/NO)
                  )))))
    document))

(defn- doc->map [document]
  (into {} (for [^Field f (.getFields document)]
             [(keyword (.name f)) (.stringValue f)])))

(defn add [index document document-field-options]
  "Add document to an index"
  (with-open [index-writer (index-writer index)]
    (.addDocument index-writer (map->doc document document-field-options))))

(defn search
  "Search index with query"
  ([index default-field query max-results]
    (search index default-field query max-results nil))
  ([index default-field query max-results sort-options]
    (with-open [reader (index-reader index)]
      (let [searcher (IndexSearcher. reader)
            parser (QueryParser. (name default-field) analyzer)
            query (.parse parser query)
            hits (if (nil? sort-options)
                   (.search searcher query (int max-results))
                   (.search searcher
                            query
                            (int max-results)
                            (Sort. (SortField. (name (:name sort-options)) (:type sort-options) (:reverse sort-options)))))]
        (doall
          (with-meta
            (map #(doc->map (.doc ^IndexSearcher searcher (.doc ^ScoreDoc %))) (.scoreDocs hits))
            {:total-results (.totalHits hits)}))))))

(defn delete-all [index]
  "Delete all document in a query"
  (with-open [writer (index-writer index)]
    (.deleteAll writer)
    (.commit writer)))

(defn escape [s]
  "Escape invalid characters form a string to be used in a query"
  (if (nil? s)
    ""
    (QueryParser/escape s)))

(defn search-range
  "Search index with query, returning results from the start position untill max-results or no more results"
  ([index default-field query start max-results]
    (search-range index default-field query start max-results nil))
  ([index default-field query start max-results sort-options]
    (let [results (search index default-field query (+ start max-results) sort-options)
          meta-data (meta results)]
      (with-meta (drop start results) meta-data))))
