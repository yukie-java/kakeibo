# 家計簿アプリ（kakeibo）

## 概要
Java（Servlet / JSP）と JDBC（H2）を用いて作成した家計簿Webアプリです。
ログイン後に支出を登録・一覧表示・削除・集計・予算管理を行えます。

本アプリは 家計管理を想定した実務レベルのCRUDアプリ として設計・実装しました。

·MVCアーキテクチャによる責務分離

·DAOパターンによるDBアクセス抽象化

·PRGパターンによる安全な画面遷移

·予算管理機能によるデータ可視化

を意識して開発しています。

---

## 主な機能

### 認証
- ログイン / ログアウト（セッション管理）

### 支出管理
- 支出登録（date / category / amount / memo）
- 支出一覧表示（新しい順）
- 支出削除

### 集計機能
- 合計金額表示（SUM）
- カテゴリ別合計（GROUP BY）
- 月別合計（yyyy-MM集計）

### 予算管理
- 月予算登録
- 先月・今月・来月の予算表示
- 予算未登録アラート表示
- 支出に対する予算達成率バー表示
- 予算オーバー警告表示

### データ連携
- CSV出力
- 条件指定CSV出力（月・カテゴリ）

---

## 画面

| URL | 内容 |
|------|------|
| /LoginServlet | ログイン画面 |
| /MainServlet | 支出管理・集計・予算確認 |

---

## 技術スタック

- Java 21
- Apache Tomcat 10
- Servlet / JSP
- JDBC
- H2 Database（Embedded）
- MVCアーキテクチャ
- DAOパターン
- HTML / CSS


---

## システム構成図

```
Browser
↓ HTTP Request
Servlet（Controller）
↓ 業務ロジック制御
DAO
↓ JDBC
H2 Database

```

### 役割説明

| レイヤ | 役割 |
|----------|----------------|
| Browser | 画面表示・ユーザー操作 |
| Servlet | リクエスト処理・画面遷移制御 |
| DAO | DBアクセス処理 |
| Database | データ永続化 |


---


## MVCアーキテクチャ

本アプリは MVC アーキテクチャを採用しています。

| 役割 | 実装 |
|--------|----------------|
| Model | Expense / Budget / ExpenseDAO / BudgetDAO |
| View | main.jsp / login.jsp / budget.jsp |
| Controller | MainServlet / LoginServlet / BudgetServlet / CsvServlet |


### 設計意図
- 画面表示とビジネスロジックを分離し保守性を向上
- DBアクセス処理をDAOに集約
- Servletを画面遷移制御に特化

---

## クラス構成図

```
Controller
├── LoginServlet
├── MainServlet
├── BudgetServlet
└── CsvServlet

DAO
├── ExpenseDAO
└── BudgetDAO

Model
├── Expense
└── Budget

View
├── login.jsp
├── main.jsp
└── budget.jsp

```



### 役割説明


| クラス | 役割 |
|------------|----------------|
| Servlet群 | 画面遷移・リクエスト制御 |
| DAO群 | DBアクセス処理 |
| Model群 | データ保持 |

---
## パッケージ構成図

```
kakeibo
├── servlet
│   ├── LoginServlet
│   ├── MainServlet
│   ├── BudgetServlet
│   └── CsvServlet
│
├── dao
│   ├── ExpenseDAO
│   └── BudgetDAO
│
├── model
│   ├── Expense
│   └── Budget
│
└── WEB-INF
    └── jsp
        ├── login.jsp
        ├── main.jsp
        └── budget.jsp

```

---

## DB接続情報

| 項目 | 内容 |
|--------|----------------|
| JDBC URL | jdbc:h2:~/kakeibo |
| DBユーザー | sa |
| パスワード | （空） |


---

## DB構成

### expenses（支出テーブル）

| カラム | 型 | 説明 |
|---|---|---|
| id | INT | 主キー |
| user_id | VARCHAR | ユーザーID |
| date | DATE | 支出日 |
| category | VARCHAR | カテゴリ |
| amount | INT | 金額 |
| memo | VARCHAR | メモ |

---

### budgets（予算テーブル）

| カラム | 型 | 説明 |
|---|---|---|
| id | INT | 主キー |
| user_id | VARCHAR | ユーザーID |
| type | VARCHAR | month / week / category |
| target | VARCHAR | 対象年月やカテゴリ |
| amount | INT | 予算金額 |

---
## ER図

```
[users]（※将来拡張想定）
   |
   | user_id
   |
   +-------------------+
   |                   |
[expenses]          [budgets]

expenses
- id (PK)
- user_id (FK)
- date
- category
- amount
- memo

budgets
- id (PK)
- user_id (FK)
- type
- target
- amount
```


### 関係説明

* 1ユーザーは複数の支出を持つ
* 1ユーザーは複数の予算を持つ
* 予算は type により月・週・カテゴリを識別する
※ users テーブルは将来的なユーザー管理拡張を想定した論理設計として定義している。



---

## 処理フロー

### ログイン処理

```
ユーザー
   ↓
LoginServlet（Controller）
   ↓
認証処理
   ↓
セッション保存
   ↓
MainServletへ遷移
```

---

### 支出登録処理

```
ユーザー入力
   ↓
MainServlet（POST）
   ↓
Expense生成（Model）
   ↓
ExpenseDAO.insert()
   ↓
DB保存
   ↓
PRGパターンでMainServletへリダイレクト
PRG（Post/Redirect/Get）パターンにより二重送信を防止

```

---

### 支出表示処理

```
MainServlet（GET）
   ↓
ExpenseDAO.findForCsv()
   ↓
ExpenseDAO.sumForCsv()
   ↓
BudgetDAO.findMonthlyBudget()
   ↓
JSP表示
```

---

### CSV出力処理

```
CsvServlet
   ↓
ExpenseDAO.findForCsv()
   ↓
CSV生成
   ↓
ブラウザダウンロード
```

---

### 予算登録処理

```
BudgetServlet
   ↓
BudgetDAO.saveMonthlyBudget()
   ↓
DB保存
   ↓
MainServletへリダイレクト
```

---


## セットアップ手順（ローカル）

### ① H2 DB作成

```
jdbc:h2:~/kakeibo
```

---

### ② テーブル作成

 ```sql
 CREATE TABLE expenses (
 id INT AUTO_INCREMENT PRIMARY KEY,
 user_id VARCHAR(50),
 date DATE,
 category VARCHAR(50),
 amount INT,
 memo VARCHAR(200)
 );

 CREATE TABLE budgets (
 id INT AUTO_INCREMENT PRIMARY KEY,
 user_id VARCHAR(50),
 type VARCHAR(20),
 target VARCHAR(20),
 amount INT
 );
 ```

---

### ③ Tomcat起動

ブラウザで以下にアクセス

http://localhost:8080/kakeibo/LoginServlet

---

## 工夫・改善点

### 設計
- MVC構成（Servlet / JSP / DAO）で責務を分離
- DAOパターンを採用しDBアクセスを抽象化
- セッション管理で認証状態を安全に保持

### 安全性
- PRG（Post/Redirect/Get）パターンを採用
- NULLデータ対策により0円表示を保証

### パフォーマンス
- 画面表示とビジネスロジックを分離し保守性を向上

### UI / UX
- 合計金額をカードUIで表示し視認性向上
- 予算達成率バーで支出状況を可視化
- 予算未登録時のアラート表示
- 操作ボタンを画面上部に統一配置
- カテゴリ入力をプルダウン形式に変更

### 実務想定機能
- CSVダウンロード機能
- 条件指定CSV出力（月・カテゴリ）

---

## 今後の改善点

- 週予算管理機能の追加
- カテゴリ別予算管理機能
- 期間指定検索（日付範囲）
- 入力値バリデーション強化
- CSVフォーマット改善
- UIレスポンシブ対応（スマホ表示）
- カテゴリマスタ管理
- グラフ表示（Chart.js等）
- 複数ユーザー対応

---

## 学んだこと

- Servlet / JSP を用いたWebアプリ開発
- MVCアーキテクチャ設計
- DAOパターンによるDBアクセス設計
- JDBCを利用したCRUD処理
- セッション管理による認証制御
- SQL集計処理（SUM / GROUP BY）
- PRGパターンによる安全な画面遷移
- CSV出力によるデータ連携
- 予算管理ロジック設計
- UIによるデータ可視化
   