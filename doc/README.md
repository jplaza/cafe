# Cafe Ecommerce Platform

## Store core

This project is intended to be the foundation of Clojure-based ecommerce software
codenamed Cafe.

## Modelo de datos (Cafe core v0.1.0)

### store
* email
* name
* templates

### configuration

### products

### categories

### customers

### order
* id :integer
* number :string
* status :integer
* customer :integer
* date_purchased :datetime
* last_modified :datetime
* payment_method :string

### shipments

Una orden tiene uno o mas shipments.
un shipment tiene 1 o mas productos.
un shipment tiene numero, tracking number, fecha de env&iacute;o, status, m&eacute;todo de env&iacute;o

* id :integer
* order_id :integer
* number :string
* tracking_number :string
* shiped_at :datetime
* status :integer
* cost :float

### order\_line\_items

### order_totals
* id :integer
* order_id :integer
* value :float
* type :string

### modules
* module_name :string (machine readable)
* name :string
* type :enumeration

### coupons


## License

Copyright &copy; 2012 - Eduardo Raad @eraad, Juan Antonio Plaza @jplazaarguello