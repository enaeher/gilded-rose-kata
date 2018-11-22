(ns gilded-rose.core)

(defn sulfuras? [{:keys [name]}]
  (= "Sulfuras, Hand of Ragnaros" name))

(defn update-item-quality [item]
  (if (not (sulfuras? item))
    (update item :sell-in dec)
    item))

(defn backstage-pass? [{:keys [name]}]
  (= "Backstage passes to a TAFKAL80ETC concert" name))

(defn aged-brie? [{:keys [name]}]
  (= "Aged Brie" name))

(defn standard-item? [item]
  (and (not (backstage-pass? item))
       (not (aged-brie? item))
       (not (sulfuras? item))))

(defn update-item-quality-special-cases [{:keys [sell-in] :as item}]
  (cond
    (and (< sell-in 0)
         (backstage-pass? item))
    (merge item {:quality 0})
    (or (aged-brie? item) (backstage-pass? item))
    (if (and (backstage-pass? item)
             (>= sell-in 5)
             (< sell-in 10))
      (merge item {:quality (inc (inc (:quality item)))})
      (if (and (backstage-pass? item)
               (>= sell-in 0) (< sell-in 5))
        (merge item {:quality (inc (inc (inc (:quality item))))})
        (if (< (:quality item) 50)
          (merge item {:quality (inc (:quality item))})
          item)))
    (< sell-in 0)
    (if (standard-item? item)
      (merge item {:quality (- (:quality item) 2)})
      item)
    (standard-item? item)
    (merge item {:quality (dec (:quality item))})
    :else item))

(defn update-quality [items]
  (map
   (comp update-item-quality-special-cases update-item-quality)
   items))

(defn item [item-name, sell-in, quality]
  {:name item-name, :sell-in sell-in, :quality quality})

(defn update-current-inventory[]
  (let [inventory 
        [(item "+5 Dexterity Vest" 10 20)
         (item "Aged Brie" 2 0)
         (item "Elixir of the Mongoose" 5 7)
         (item "Sulfuras, Hand Of Ragnaros" 0 80)
         (item "Backstage passes to a TAFKAL80ETC concert" 15 20)]]
    (update-quality inventory)))
