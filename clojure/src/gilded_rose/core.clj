(ns gilded-rose.core)

(defn sulfuras? [{:keys [name]}]
  (= "Sulfuras, Hand of Ragnaros" name))

(defn update-item-sell-in [item]
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

(defn update-item-quality [{:keys [sell-in quality] :as item}]
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
                             :else (dec quality)))))

(defn update-quality [items]
  (map
   (comp update-item-quality update-item-sell-in)
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
