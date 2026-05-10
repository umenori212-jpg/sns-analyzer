# SNS動画分析AI

SNSやショート動画をアップロードするだけで、AIがジャンル・ターゲット・英語投稿文・ハッシュタグを自動生成するWebアプリです。

---

## 画面イメージ

### アップロード画面
動画または画像をドラッグ&ドロップするだけで分析がスタートします。

### 分析結果画面
- 動画ジャンル
- バズりやすさスコア
- 想定ターゲット
- 英語投稿文（日本語訳付き）
- おすすめ英語ハッシュタグ5個

---

## 使用技術

| カテゴリ | 技術 |
|---------|------|
| バックエンド | Java 25 / Spring Boot 3.4.5 |
| フロントエンド | Thymeleaf / Bootstrap 5 |
| AI | Google Gemini API（gemini-2.5-flash） |
| ビルド | Maven |

---

## 機能

- MP4・MOV・AVI・JPG・PNG のアップロード対応
- Google Gemini APIによる動画内容の自動分析
- 英語投稿文の自動生成（日本語訳付き）
- SNS向けおすすめハッシュタグ5個を自動生成
- ワンクリックコピー機能

---

## ローカルでの起動方法

### 必要な環境
- Java 17以上
- Maven 3.8以上
- Google Gemini APIキー（無料取得可能）

### 手順

**1.リポジトリをクローン**
```bash
git clone https://github.com/umenori212-jpg/sns-analyzer.git
cd sns-analyzer
```

**2.APIキーを設定**

`src/main/resources/application.properties` を作成して以下を記述：
```properties
spring.application.name=sns-analyzer
server.port=8080
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=200MB
spring.servlet.multipart.max-request-size=200MB
gemini.api.key=YOUR_GEMINI_API_KEY
spring.thymeleaf.cache=false
```

**3.起動**
```bash
mvn spring-boot:run
```

**.ブラウザでアクセス**
```
http://localhost:8080
```

---

## Gemini APIキーの取得方法

1. https://aistudio.google.com/apikey にアクセス
2. Googleアカウントでログイン
3. 「Create API key」をクリック
4. 発行されたキーを application.properties に設定

無料で利用できます。
