# Recommendation Service

## Muc tieu

`recommendation-service` hien tai duoc rut gon con 2 nhanh:

1. `ProductPopularity`
2. `ProductAssociation`

Khong con `UserPreference`.

Muc tieu la lam he thong goi y don gian, de chay, de mo rong sau:

- Landing page: hien `Mon pho bien`
- Product detail: hien `Mon lien quan` va `Topping goi y`

## 1. ProductPopularity

Document: `product_popularity`

Luu do pho bien tong hop cua tung mon:

- `productId`
- `viewCount`
- `clickCount`
- `orderCount`
- `totalScore`
- `lastUpdated`

Dung cho:

- block `Mon pho bien` tren landing page

### Scoring

- `product_viewed` -> `+1`
- `product_clicked` -> `+2`
- `order_placed` -> `+10 * quantity`

### Decay

Khong reset trang moi thang.

Thay vao do, moi thang chay job decay:

```text
newScore = oldScore * 0.5
```

Mac dinh:

- `recommendation.popularity.decay-factor=0.5`
- `recommendation.popularity.decay-cron=0 0 0 1 * *`

Y nghia:

- mon cu van con anh huong
- nhung diem cu se giam dan
- mon moi co co hoi len top

## 2. ProductAssociation

Document: `product_associations`

Luu moi lien he cua 1 mon voi:

- mon khac
- option value khac

Field:

- `productId`
- `associatedTargetId`
- `associationType`
- `correlationScore`
- `lastUpdated`

### Association types

- `PRODUCT`
- `OPTION_VALUE`

### Dung cho

#### PRODUCT

Hien:

- `Mon lien quan`
- `Khach hay mua kem`

#### OPTION_VALUE

Hien:

- `Topping goi y`
- `Option value goi y`

### Scoring

- `PRODUCT` trong cung order -> `+5`
- `OPTION_VALUE` duoc chon cho product -> `+3`

## Event flow

### 1. Product viewed

Topic:

- `product-viewed`

Payload:

```json
{
  "productId": 2001
}
```

Tac dong:

- cap nhat `ProductPopularity`

### 2. Product clicked

Topic:

- `product-clicked`

Payload:

```json
{
  "productId": 2001
}
```

Tac dong:

- cap nhat `ProductPopularity`

### 3. Order placed

Topic:

- `order-placed`

Payload:

```json
{
  "orderId": "ORD-001",
  "userId": 1001,
  "items": [
    {
      "productId": 2001,
      "categoryId": 3001,
      "quantity": 2,
      "optionValueIds": [4001, 4002]
    },
    {
      "productId": 2002,
      "categoryId": 3002,
      "quantity": 1,
      "optionValueIds": []
    }
  ]
}
```

Tac dong:

- cap nhat `ProductPopularity`
- cap nhat `ProductAssociation(PRODUCT)`
- cap nhat `ProductAssociation(OPTION_VALUE)`

Luu y:

- `categoryId` hien tai khong con duoc dung trong scoring
- giu lai trong payload cung duoc, nhung khong bat buoc cho luong recommendation hien tai

## FE/BE flow

### A. Landing page

FE goi:

- `GET /recommendations/popular`

BE:

- query `ProductPopularity`
- sort:
  - `totalScore desc`
  - `lastUpdated desc`

BE tra:

- list `productIds`

FE:

- goi `product-service` lay thong tin card mon
- render block `Mon pho bien`

Response hien tai tu recommendation-service:

- `productId`
- `viewCount`
- `clickCount`
- `orderCount`
- `totalScore`
- `lastUpdated`

### B. Product detail page

FE goi:

- `GET /products/{productId}`
- `GET /recommendations/products/{productId}/related`
- `GET /recommendations/products/{productId}/option-values`

BE:

- `related` -> query `ProductAssociation` voi `associationType = PRODUCT`
- `option-values` -> query `ProductAssociation` voi `associationType = OPTION_VALUE`

FE:

- render `Mon lien quan`
- render `Topping goi y`

## Public APIs hien tai

Base path:

- `/public/recommendations`

Endpoints:

1. `GET /public/recommendations/popular`
2. `GET /public/recommendations/products/{productId}/related`
3. `GET /public/recommendations/products/{productId}/option-values`

## Fallback

Ban dau khi chua co data:

### Landing

Neu `ProductPopularity` rong:

- fallback sang `product-service`
- lay danh sach product active/noi bat/newest

### Product detail

Neu `ProductAssociation(PRODUCT)` rong:

- fallback sang product cung category / gan gia

Neu `ProductAssociation(OPTION_VALUE)` rong:

- fallback rong
- hoac dung option mac dinh sap xep san

## Ket luan

Phien ban hien tai uu tien:

- don gian
- de van hanh
- de giai thich

Khong co personalized theo user.

Neu can ca nhan hoa sau nay, co the them lai nhanh `UserPreference` nhu mot layer moi, khong can sua 2 nhanh hien tai.
