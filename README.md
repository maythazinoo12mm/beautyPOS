# beautyPOS

美容品販売店のレジ業務（商品検索・カート・会計・在庫更新）を模した Spring Boot アプリケーションです。
`spring-boot-practice` を土台に、PostgreSQL（本番想定DB）・Flywayによるスキーマ管理・Git管理を追加した構成になっています。

## 構成

- Java 21 / Spring Boot 3.5.13 / Gradle
- Spring Web, Spring Data JPA, Bean Validation, Thymeleaf
- **PostgreSQL**（本番想定。Flywayでスキーマ・初期データを管理）
- テストのみ H2（インメモリ）で高速に実行

```
src/main/java/com/example/practice/
├── SpringBootPracticeApplication.java
├── WebConfig.java
├── item/    商品マスタ（画面 /items ＋ REST API /api/items）
└── sale/    レジ（画面 /pos, /pos/payment, /pos/receipt, /sales）

src/main/resources/db/migration/
├── V1__init_schema.sql   テーブル定義（item / sale / sale_item）
└── V2__seed_data.sql     初期データ（美容品6件）
```

## セットアップ（初回のみ）

### 1. PostgreSQLのインストール

Windowsにネイティブインストールします（未実施の場合）。

```powershell
winget install PostgreSQL.PostgreSQL
```

インストーラでも可（[postgresql.org](https://www.postgresql.org/download/windows/) から取得）。
インストール時に設定した `postgres` ユーザーのパスワードは控えておいてください。

### 2. データベース・ユーザーの作成

インストール後、`psql` で以下を実行します（Windowsスタートメニューの「SQL Shell (psql)」でも可）。

```sql
CREATE DATABASE beautypos;
CREATE USER beautypos_user WITH PASSWORD 'beautypos_pass';
GRANT ALL PRIVILEGES ON DATABASE beautypos TO beautypos_user;
```

### 3. 接続情報の設定（必要な場合のみ）

`src/main/resources/application.properties` のデフォルト値（`beautypos` / `beautypos_user` / `beautypos_pass`）を
そのまま使う場合は変更不要です。変更したい場合は環境変数で上書きできます。

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/beautypos"
$env:DB_USERNAME = "beautypos_user"
$env:DB_PASSWORD = "beautypos_pass"
```

## 使い方

1. 起動:
   ```
   .\gradlew.bat bootRun
   ```
   起動時にFlywayが `V1__init_schema.sql` / `V2__seed_data.sql` を自動適用します（2回目以降は差分なし）。

2. ブラウザで確認:
   - `http://localhost:8080/` → 自動的に `/pos`（レジ画面）へリダイレクト
   - `/items` 商品管理　`/sales` 売上履歴
   - `GET http://localhost:8080/api/items` REST API（JSON）

3. テスト実行（H2で完結、PostgreSQL不要）:
   ```
   .\gradlew.bat test
   ```

4. DBの中身を直接確認する場合は `psql -U beautypos_user -d beautypos` または pgAdmin を使用してください。

## Git

このプロジェクトは `git init` 済みのローカルリポジトリです。リモートリポジトリへの接続は行っていません。

```
git log        # コミット履歴の確認
git status     # 変更状況の確認
```

## 関連ドキュメント

`docs/` 配下に、業務フロー・アーキテクチャ・テスト仕様書・今回のPostgreSQL移行内容などをまとめたドキュメントがあります。
