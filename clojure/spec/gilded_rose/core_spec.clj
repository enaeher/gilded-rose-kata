(ns gilded-rose.core-spec
  (:require [clojure.test :refer :all]
            [gilded-rose.core :refer [update-quality item]]))

(deftest gilded-rose-test
  (is (= "foo" (:name (first (update-quality [(item "foo" 0 0)]))))))
