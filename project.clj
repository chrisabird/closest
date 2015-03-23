(defproject closest "0.1.0"
            :description "A Clojure interface to the Lucene search engine"
            :url "http://github/chrisabird/closest"
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [org.apache.lucene/lucene-core "5.0.0"]
                           [org.apache.lucene/lucene-queryparser "5.0.0"]
                           [org.apache.lucene/lucene-analyzers-common "5.0.0"]
                           [org.apache.lucene/lucene-highlighter "5.0.0"]]
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :profiles {:1.4  {:dependencies [[org.clojure/clojure "1.4.0"]]}
                       :1.5  {:dependencies [[org.clojure/clojure "1.5.0"]]}
                       :1.6  {:dependencies [[org.clojure/clojure "1.6.0"]]}}
            :codox {:src-dir-uri "http://github/chrisabird/closest/blob/master"
                    :src-linenum-anchor-prefix "L"})