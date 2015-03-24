(ns closest.core-test
  (:require [clojure.test :refer :all]
            [closest.core :refer :all]))

(def document-field-options
  (merge
    (text-field :name true)
    (string-field :id false)
    (sorted-doc-values-field :sort-name)))

;BinaryDocValuesField
;DoubleDocValuesField
;DoubleField
;FloatDocValuesField
;FloatField
;IntField
;LongField
;NumericDocValuesField
;SortedNumericDocValuesField
;SortedSetDocValuesField
;StoredField


(deftest test-search
  (let [index (memory-index)]
    (add index
         {:name "Arnold Rimmer"
          :id "ABC123"
          :sort-name "Arnold-Rimmer"}
         document-field-options)
    (add index
         {:name "Yvonne McGruder"
          :id "ABC456"
          :sort-name "Yvonne McGruder"}
         document-field-options)
    (add index
         {:name "Ace Rimmer"
          :id "ABC789"
          :sort-name "Ace Rimmer"}
         document-field-options)
    (testing "Exact match"
      (let [result (search index "name" "Arnold Rimmer" 1)]
        (is (= 1 (count result)))
        (is (= "Arnold Rimmer" (:name (first result))))
        (is (nil? (:id (first result))))))
    (testing "Wildcard Match"
      (let [result (search index "name" "Rimmer*" 3)]
        (is (= 2 (count result)))
        (is (= "Arnold Rimmer" (:name (first result))))
        (is (= "Ace Rimmer" (:name (second result))))))
    (testing "Queries with more results than requested"
      (let [result (search index "name" "Rimmer*" 1)]
        (is (= 1 (count result)))
        (is (= 2 (:total-results (meta result))))))
    (testing "Query for a range of results"
      (let [result (search-range index "name" "Rimm*" 1 1)]
        (is (= 1 (count result)))
        (is (= "Ace Rimmer" (:name (first result))))))
    (testing "Query for a range of results that are sorted"
      (let [sort (string-sort :sort-name false)
            result (search-range index "name" "Rimm*" 1 1 sort)]
        (is (= 1 (count result)))
        (is (= "Arnold Rimmer" (:name (first result))))))))





