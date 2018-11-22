(ns gilded-rose.core-spec
  (:require [clojure.test :refer :all]
            [gilded-rose.core :refer [update-quality item]]))

(deftest gilded-rose-test
  (is (= "foo" (:name (first (update-quality [(item "foo" 0 0)]))))))

(deftest standard-item-test
  (is (= [(item "+5 Dexterity Vest" 9 19)]
         (update-quality [(item "+5 Dexterity Vest" 10 20)]))
      "At each update, sell-in and quality decrease"))

(deftest past-sell-by-date-test
  (is (= [(item "+5 Dexterity Vest" -1 18)]
         (update-quality [(item "+5 Dexterity Vest" 0 20)]))
      "Past the sell-by date, quality decreases twice as fast"))

;; This test is failing because the existing system does not implement
;; the spec as written.
(deftest quality-will-not-go-negative
  (is (= [(item "+5 Dexterity Vest" 4 0)]
         (update-quality [(item "+5 Dexterity Vest" 5 0)]))
      "Quality is never negative"))

(deftest aged-brie
  (is (= [(item "Aged Brie" 3 5)]
         (update-quality [(item "Aged Brie" 4 4)]))
      "Brie increases in quality with age"))

(deftest aged-brie-max-quality
  (is (= [(item "Aged Brie" 3 50)]
         (update-quality [(item "Aged Brie" 4 50)]))
      "Quality doesn't exceed 50"))

(deftest sulfuras
  (is (= [(item "Sulfuras, Hand of Ragnaros" 0 80)]
         (update-quality [(item "Sulfuras, Hand of Ragnaros" 0 80)]))
      "Sulfuras never has to be sold or decreases in quality"))

(deftest backstage-passes-increase-as-sell-date-approaches
  (is (= [(item "Backstage passes to a TAFKAL80ETC concert" 14 21)]
         (update-quality [(item "Backstage passes to a TAFKAL80ETC concert" 15 20)]))
      "Backstage passes increase in quality as concert approaches"))

(deftest backstage-passes-increase-by-2-when-fewer-than-10-days
  (is (= [(item "Backstage passes to a TAFKAL80ETC concert" 9 22)]
         (update-quality [(item "Backstage passes to a TAFKAL80ETC concert" 10 20)]))
      "Backstage passes increase by 2 when there are 10 days or less till concert"))

(deftest backstage-passes-increase-by-3-when-fewer-than-5-days
  (is (= [(item "Backstage passes to a TAFKAL80ETC concert" 4 23)]
         (update-quality [(item "Backstage passes to a TAFKAL80ETC concert" 5 20)]))
      "Backstage passes increase by 3 when there are 5 days or less till concert"))

(deftest backstage-passes-drop-to-0-after-concert
  (is (= [(item "Backstage passes to a TAFKAL80ETC concert" -1 0)]
         (update-quality [(item "Backstage passes to a TAFKAL80ETC concert" 0 20)]))
      "Backstage passes drop to zero after the concert"))
