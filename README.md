Closest
=======

Closest is designed to provide a thin facade to [Lucene](http://lucene.apache.org/).

Installation
------------

To install Closest

    [closest "0.1.0"]

Usage
-----

### Require it

    (ns example
      (:require [closest.core :as closest]))

### Create an in memory index

    (def index (closest/memory-index))

### Create a index on disk

    (def index (closest/disk-index "/path/to/a/folder"))

### Define field options for your document

Before you can add documents to an index you need to define the field types and weather they will be stored e.g...

    (def document-field-options
      (merge
        (text-field :name true)
        (string-field :id false))

### Add items to a index

    (closest/add index {:name "Arnold Rimmer" :id "1"} document-field-options)

### Search and index

To search you must supply an index, default field to search, a search term and a max number of results to return

    (closest/search index "id" "1" 1)

You can also provide more complex queries

    (closest/search index "id" "name:Rimm*" 10)

### Search and return specific range

Start at 10th result and return 10 more or until there are no more results

    (closest/search index "id" "name:Rimm*" 10 10)

### Sorting by string value

Define a document with a field that can be sorted by

    (def document-field-options
      (merge
        (text-field :name true)
        (sorted-doc-values-field :name))

Add document providing a value for the sort field

    (closest/add index {:name "Arnold Rimmer" :sort-name "Arnold Rimmer"} document-field-options)
    (closest/add index {:name "Ace Rimmer" :sort-name "Arnold Rimmer"} document-field-options)

Define a sort criteria

    (def sort (string-sort :sort-name false))

Search document and sort results by criteria

    (closest/search index "id" "name:Rimm*" 10 sort)


Todo
----

 * Provide more field types
    * BinaryDocValuesField
    * DoubleDocValuesField
    * DoubleField
    * FloatDocValuesField
    * FloatField
    * IntField
    * LongField
    * NumericDocValuesField
    * SortedNumericDocValuesField
    * SortedSetDocValuesField
    * StoredField

 * Provide more sort field types
 * Provide functions to provide highlighting

## License

Copyright © 2015 Christopher Bird

Distributed under the Eclipse Public License either version 1.0
