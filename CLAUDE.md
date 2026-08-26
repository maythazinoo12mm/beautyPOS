# プロジェクト概要

beautyPOS：美容品販売店のレジ業務（商品検索・カート・会計・在庫更新）を模した Spring Boot アプリケーション。
`spring-boot-practice`（Claude Code練習用の最小限CRUD API）を土台に、PostgreSQL・Flyway・Git管理を追加した構成。

## 技術スタック

- Java 21, Spring Boot 3.5.13, Gradle
- Spring Web / Spring Data JPA / Bean Validation / Thymeleaf
- PostgreSQL（本番想定、Flywayでスキーマ管理） ／ テストのみH2（インメモリ）

## ディレクトリ構成

- `item` パッケージ: 商品マスタ（エンティティ・リポジトリ・サービス・画面コントローラ・RESTコントローラ）
- `sale` パッケージ: レジ・会計（Sale/SaleItemエンティティ、Cart（セッションスコープ）、PosController等）
- `src/main/resources/db/migration`: Flywayマイグレーション（スキーマ定義・初期データ）
- テストは `src/test/java` 配下、MockMvc を使ったコントローラテストとサービス層のテストが中心。`src/test/resources/application.properties` によりテスト実行時はH2に接続する

## コーディング方針

- サービス層に業務ロジックを置き、コントローラは薄く保つ
- 例外はドメイン例外（`ItemNotFoundException`, `DuplicateBarcodeException` 等）を投げ、コントローラ側でHTTPステータス／画面表示に変換する
- 新しいエンティティを追加する場合も同じ層構成（entity / repository / service / controller）に揃える
- スキーマ変更はHibernateの自動生成に任せず、Flywayマイグレーション（`V{n}__説明.sql`）を追加する（`spring.jpa.hibernate.ddl-auto=validate` のため）

## よく使うコマンド

- 起動: `./gradlew bootRun`（Windowsは `.\gradlew.bat bootRun`）※事前にPostgreSQLの起動とDB作成が必要（README.md参照）
- テスト: `./gradlew test`（H2で完結、PostgreSQL不要）
- ビルド: `./gradlew build`
