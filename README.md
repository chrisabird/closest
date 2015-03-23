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
    * SortedDocValuesField
    * SortedNumericDocValuesField
    * SortedSetDocValuesField
    * StoredField

 * Provide functions to provide pagination
 * Provide functions to provide highlighting
 * Provide a way to specify sorting


## License

Copyright © 2015 Christopher Bird

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
