(ns cafe.core.data.validation
  "Validation functions for entity fields")

(def error-list (atom {}))
(def rules (atom {}))

(defn validate
  "Adds a validation rule to a field"
  [field rule & [arg]]
  (swap! rules
         #(assoc-in % [field rule] {:func (resolve (symbol (name rule))) :arg arg})))

; (defn push-error
;   "Adds an error message to the list of errors"
;   [field message]
;   (swap! error-list update-in [field]))

(defn errors @error-list)

;; ## Rules
(defn presence
  "Checks the field for a value. If empty, nil or false returns false"
  [value]
  (and value (not= value "")))

(defn number
  "Returns true if the string is a number"
  [value]
  (try
    (Long/parseLong value)
    true
    (catch Exception e
      false)))

(defn greater-than
  "Checks if the value is greater than x"
  [value x]
  (> value x))

(defn greater-than-or-equal-to
  "Checks if the value is greater than or equal x"
  [value x]
  (>= value x))

(defn less-than
  "Checks if the value is less than x"
  [value x]
  (< value x))

(defn less-than-or-equal-to
  "Checks if the value is less than or equal to x"
  [value x]
  (<= value x))

(defn field-valid?
  "Performs all validations for the given field using the provided value and 
  returns a true if all rules are met. True if no rules are defined for field"
  [field value]
  (if (contains? @rules field)
    (reduce #(and %1 %2) (map #(if (nil? (:arg %))
                                  (apply (:func %) (list value))
                                  (apply (:func %) (list value (:arg %))))
                              (vals (get @rules field {}))))
    true))

(defn valid?
  "Performs all validations and returns a true if all rules are met"
  [data]
  (let [kvl (map vector (keys data) (vals data))]
    (reduce #(and %1 %2)
            (map #(field-valid? (% 0) (% 1))
                 kvl))))
