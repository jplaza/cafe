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
* number :text
* status :integer
* customer :integer
* date_purchased :datetime
* last_modified :datetime
* payment_method :text

### shipments

An order has one or more shipments.

A shipment has one or more products.

A shipment has:

* id :integer
* order_id :reference orders
* number :text
* tracking_number :text
* shiped_at :datetime
* status :integer
* cost :float
* method :reference shipping_methods

### shipment_items

* id :integer
* product_id :reference products (variant)
* status :text
* shipment_id :reference shipment

### payments

An order has one or more payments

A payment has:

* id :integer
* order_id :reference orders
* payment_method :reference payment_methods
* amount :float
* status :string
* created_at :datetime
* updated_at :datetime

### payment_methods

* id :integer
* name :text
* description :text
* enabled :boolean
* created_at :datetime
* updated_at :datetime

### order\_line\_items

### order_totals
* id :integer
* order_id :integer
* value :float
* type :text

### modules
* module_name :text (machine readable)
* name :text
* type :enumeration

### coupons


## License

Copyright &copy; 2012 - Eduardo Raad @eraad, Juan Antonio Plaza @jplazaarguello