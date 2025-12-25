# 📦 Product Price Manager

MUST READ ↓ 

!!! CLICK productpriceupdater --> CLICK src --> CLICK productpriceupdater --> CLICK Productpriceupdate.java !!!

**Add → Update → View** products in your inventory with Java + MySQL.

## ⚡ Quick Setup

```sql
-- One-time database setup
CREATE DATABASE product_db;
USE product_db;
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    price DOUBLE
);
```

```bash
# Run the program
javac -cp ".;mysql-connector-j.jar" ProductManager.java
java -cp ".;mysql-connector-j.jar" ProductManager
```

## 🎯 What It Does
- **➕ Add** new products (prevents duplicates)
- **🔄 Update** prices of existing products
- **👀 View** complete inventory with stats

