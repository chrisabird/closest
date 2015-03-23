(ns closest.core-test
  (:require [clojure.test :refer :all]
            [closest.core :refer :all]))

(def document-field-options
  (merge
    (text-field :name true)
    (string-field :id false)))

;BinaryDocValuesField
;DoubleDocValuesField
;DoubleField
;FloatDocValuesField
;FloatField
;IntField
;LongField
;NumericDocValuesField
;SortedDocValuesField
;SortedNumericDocValuesField
;SortedSetDocValuesField
;StoredField
;StringField
;TextField

(deftest test-search
  (let [index (memory-index)]
    (add index
         {:name "Arnold Rimmer"
          :id "ABC123"}
         document-field-options)
    (add index
         {:name "Yvonne McGruder"
          :id "ABC456"}
         document-field-options)
    (add index
         {:name "Ace Rimmer"
          :id "ABC789"}
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
        (is (= 2 (:total-results (meta result))))))))





