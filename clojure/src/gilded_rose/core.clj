(ns gilded-rose.core)

;; Item type helpers

(defn sulfuras? [{:keys [name]}]
  (= "Sulfuras, Hand of Ragnaros" name))

(defn backstage-pass? [{:keys [name]}]
  (= "Backstage passes to a TAFKAL80ETC concert" name))

(defn aged-brie? [{:keys [name]}]
  (= "Aged Brie" name))

(defn standard-item? [item]
  (and (not (backstage-pass? item))
       (not (aged-brie? item))
       (not (sulfuras? item))))

;; Nightly update helpers for items

(defn update-item-sell-in
  "Updates the sell-in date for a single item."
  [item]
  (if (not (sulfuras? item))
    (update item :sell-in dec)
    item))

(defn update-item-quality
  "Updates the quality for a single item."
  [{:keys [sell-in quality] :as item}]
  (assoc
   item :quality
   (cond
     (backstage-pass? item) (cond
                              (< sell-in 0) 0
                              (> 5 sell-in -1) (+ quality 3)
                              (> 10 sell-in 4) (+ quality 2)
                              :else (inc quality))

     (aged-brie? item) (min (inc quality) 50)

     (sulfuras? item) quality

     (standard-item? item) (cond
                             (< sell-in 0) (- quality 2)
                             :else (max (dec quality) 0)))))

(defn apply-conjuring
  "Takes an update-fn and a conjured item. Applies the update-fn, then
  doubles any decrease in quality in the updated item."
  [update-fn {:keys [quality] :as item}]
  (let [{updated-quality :quality :as updated-item} (update-fn item)
        quality-decrease (- quality updated-quality)]
    (if (pos? quality-decrease)
      (assoc updated-item :quality (max (- updated-quality quality-decrease) 0))
      updated-item)))

(defn maybe-apply-conjuring
  "Takes an update-fn and an item. If the item is conjured, apply
  conjuring, otherwise call the update-fn directly."
  [update-fn {:keys [conjured?] :as item}]
  (if conjured?
    (apply-conjuring update-fn item)
    (update-fn item)))

(defn update-quality
  "Nightly inventory update. Takes a sequence of items and returns a
  sequence of updated items."
  [items]
  (map
   (partial maybe-apply-conjuring (comp update-item-quality update-item-sell-in))
   items))

(defn item [item-name, sell-in, quality]
  {:name item-name, :sell-in sell-in, :quality quality})

(defn conjure
  "Takes an item and returns an equivalent conjured item."
  [item]
  (assoc item :conjured? true))

(defn update-current-inventory[]
  (let [inventory 
        [(item "+5 Dexterity Vest" 10 20)
         (item "Aged Brie" 2 0)
         (item "Elixir of the Mongoose" 5 7)
         (item "Sulfuras, Hand Of Ragnaros" 0 80)
         (item "Backstage passes to a TAFKAL80ETC concert" 15 20)]]
    (update-quality inventory)))
