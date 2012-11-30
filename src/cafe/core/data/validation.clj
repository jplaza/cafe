(ns cafe.core.data.validation
  "Validation functions for entity fields"
  (:use clojure.set))

(declare add-error)

(def errors (atom {}))
(def rules (atom {}))

;; ## Validation functions

(defn field-valid?
  "Performs all validations for the given field using the provided value and 
  returns a true if all rules are met. Also true if no rules are defined for field"
  [field value]
  (if (contains? @rules field)
    (reduce #(and %1 %2) (map #(if (nil? (:arg %))
                                  (apply (:func %) (list field value))
                                  (apply (:func %) (list field value (:arg %))))
                              (vals (field @rules))))
    true))

(defn record-valid?
  "Performs all validations and returns a true if all rules are met."
  [data]
  (let [kvl (map vector (keys data) (vals data))
        data-keys (-> data keys set)
        rules-keys (-> @rules keys set)]
    (reset! errors {})
    (if (= (count (intersection rules-keys data-keys)) (count @rules))
      (reduce #(and %1 %2)
              (map #(field-valid? (% 0) (% 1))
                   kvl))
      (do
        (add-error "incomplete record")
        false))))

(defn validate
  "Adds a validation rule to the set.
  rule must be one of the following keywords
  __:presence__
  __:number__
  __:greater-than__
  __:greater-than-or-equal-to__
  __:less-than__
  __:less-than-or-equal-to__
  fields can be a keyword which represents the name of the field, or a vector of
  keywords of field names:

    (validate :name :presence)
    (validate [:age :id] :number)
  "
  [fields rule & [arg]]
  (doseq [field (if-not (vector? fields) (conj [] fields) fields)]
    (swap! rules
           #(assoc-in % [field rule] {:func (resolve (symbol (name rule))) :arg arg}))))

(defmacro defvalid
  "Defines two helper functions, valid? and get-errors.
  In order for a record to be valid these conditions must me met:

  - All rules should be passed
  - The record should provide valid data for a field if a rule for it is defined"
  [record & body]
  (let [record# record]
    `(do
      (do ~@body)
      (defn ~(symbol "get-errors") [] (deref ~'errors))
      (defn ~(symbol "valid?") [~'data]
        (record-valid? ~'data)))))

(defn add-error
  "Adds an error message to the list of errors. If not field is specified uses
  :base. Base is the error namespace for general errors"
  [message & [field]]
  (swap! errors
         #(assoc % (or field :base) (conj (get % (or field :base) []) message))))

;; ## Rules
;; Set of available rules for validation
(defn validation-rule
  "Helper function that adds common functionality to the rules functions"
  [predicate message & [field]]
  (if-not predicate
    (do
      (add-error message field)
      false)
    true))

(defn presence
  "Checks the field for a value. If empty, nil or false returns false"
  [field value]
  (validation-rule (and value (not= value "")) "can't be empty" field))

(defn number
  "Returns true if the string is a number"
  [field value]
  (try
    (Long/parseLong value)
    true
    (catch Exception e
      (add-error "is not a number" field)
      false)))

(defn greater-than
  "Checks if the value is greater than x"
  [field value x]
  (validation-rule (> value x) (str "must be greater than " x) field))

(defn greater-than-or-equal-to
  "Checks if the value is greater than or equal x"
  [field value x]
  (validation-rule (>= value x) (str "must be greater than or equal to " x) field))

(defn less-than
  "Checks if the value is less than x"
  [field value x]
  (validation-rule (< value x) (str "must be less than " x) field))

(defn less-than-or-equal-to
  "Checks if the value is less than or equal to x"
  [field value x]
  (validation-rule (<= value x) (str "must be less than or equal to " x) field))
