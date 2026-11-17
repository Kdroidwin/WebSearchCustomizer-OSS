# Web Search Customizer (OSS 版)

他アプリの「Web 検索」(`ACTION_WEB_SEARCH`) や共有メニュー (`ACTION_SEND`) を横取りして、好きな検索エンジンで検索結果をブラウザに開く Android アプリです。Material You (Material 3) 対応のオープンソース再実装です。

> 原作: [Web Search Customizer](https://play.google.com/store/apps/details?id=com.brouken.websearch) by Marcel Dopita (brouken.com)
> 本リポジトリは原作に着想を得た**独立したオープンソース実装**であり、原作作者とは関係ありません。

## 特徴

- 🎨 **Material You (ダイナミックカラー)** — 壁紙に合わせた動的配色 (Android 12+、オン/オフ切替可)
- 🌗 **4 つのテーマ**: システムに従う / ホワイト / ダーク / **AMOLED ブラック (純黒)**
- 🔍 内蔵検索エンジン: DuckDuckGo / Google / Bing / Yahoo! JAPAN / Brave / Startpage / Ecosia / Qwant / Mojeek / SearXNG / Wikipedia (ja) ＋ **カスタム URL**
- 🔗 カスタムテンプレートは `%s` と `{query}` 両対応。検索ワードは自動で URL エンコード
- 🧪 「検索テスト」ボタンで実際の `ACTION_WEB_SEARCH` インテントを発行して動作確認
- 📱 Android 13+ のテーマアイコン (モノクローム) 対応アダプティブアイコン
- 🪶 軽量 (R8 最適化済み、約 1.5 MB) / 権限なし / 広告なし / 追跡なし
- 🌐 日本語 / 英語 UI

## 使い方

1. アプリをインストールしたら、このアプリ内で既定の検索エンジンを選びます。
2. メールアプリや WebView アプリなどの「**Web 検索**」メニューを実行すると、ハンドラ選択画面が出ますので「**Web Search Customizer**」を選んで「**常に**」にします。
   ※ Chrome など一部アプリはこの動作を上書きします。
3. テキストを共有メニューから直接送ることでも検索できます。
4. 「検索テスト」を実行すると、実際の `ACTION_WEB_SEARCH` が発行され、設定したエンジンで検索されます。

## テーマ

| テーマ | 説明 |
|---|---|
| システムに従う | OS のダークモード設定に追従 |
| ホワイト (ライト) | 常に純白ベース |
| ダーク | 常にダークベース |
| AMOLED ブラック | 純黒 `#000000` ベース (有機 EL 省電力) |

「Material You カラー」スイッチで、各テーマの **アクセント色** を壁紙ベースのダイナミックカラーに差し替えられます (Android 12 以降)。表面色 (ニュートラル) は選択中のテーマが常に優先されます。

## ビルド方法

必要環境:

- JDK 17 以上
- Android SDK (compileSdk 36 / AGP 9.0)
- インターネット接続 (初回は依存ライブラリをダウンロード)

```bash
git clone <this repo>
cd websearchcustomizer-oss
./gradlew :app:assembleRelease
```

デバッグビルド:

```bash
./gradlew :app:assembleDebug
```

### 署名 (リリース)

リリース APK の署名は `keystore/keystore.properties` がある場合のみ自動適用されます (ファイルは `.gitignore` 済み)。独自の鍵で署名するには:

```bash
keytool -genkeypair -keystore keystore/release.jks -storetype PKCS12 \
  -alias <alias> -keyalg RSA -keysize 4096 -validity 10950
```

`keystore/keystore.properties` を作成:

```properties
storeFile=keystore/release.jks
storePassword=...
keyAlias=...
keyPassword=...
```

`keystore/keystore.properties` が無い場合は未署名の `app-release-unsigned.apk` が生成されます (CI でも同様)。

## プロジェクト構成

```
app/src/main/java/io/github/websearchcustomizer/
├── App.kt                        # アプリ全体のナイトモード適用
├── Prefs.kt                      # 設定キー / 既定値
├── SettingsActivity.kt           # 設定画面 (Material 3 + ダイナミックカラー)
├── GeneralPreferenceFragment.kt  # 設定ロジック (エンジン / テーマ / About)
├── SearchActivity.kt             # WEB_SEARCH / SEND を受けて検索 URL を開く
└── LinkProxyActivity.kt          # https リンクの受け渡し (明示指定時のみ)
```

- `SearchActivity` — `ACTION_WEB_SEARCH` の `query` extra と `ACTION_SEND` (text/plain) を処理します。検索ワードは UTF-8 で URL エンコードされ、テンプレートの `%s` / `{query}` に差し込まれます。
- `LinkProxyActivity` — 原本の互換性のため維持されているリンクの仲介アクティビティです。意図的に `CATEGORY_DEFAULT` / `BROWSABLE` を宣言していないため、**既定ブラウザに選択されることはなく**、明示的に指定された場合のみ、自分自身を除外した安全な転送を行います (原本の無限ループ問題を解消)。

## OSS 版で変更・改善した点

- `android.preference` (非推奨 API) → AndroidX Preference + Material 3 へ全面移行
- 検索ワードの URL エンコードを追加 (原本は生文字列をそのまま挿入しており、空白を含む検索が壊れるケースがありました)
- カスタム URL に `{query}` プレースホルダーを追加対応
- テーマ選択 (ホワイト / ダーク / AMOLED) と Material You ダイナミックカラーの追加
- minSdk 23 (Material 1.14 の要件) / targetSdk 36

## ライセンス

[MIT License](LICENSE)

## 謝辞

- 原作アプリとその設計思想: **Marcel Dopita (brouken.com)**
- [Material Components for Android](https://github.com/material-components/material-components-android)

---

# Web Search Customizer (OSS edition)

An Android app that intercepts other apps' "Web search" (`ACTION_WEB_SEARCH`) and share menu (`ACTION_SEND`) and opens the query in your preferred search engine. A Material You (Material 3) enabled open-source reimplementation of the original app.

**Features:** Material You dynamic colors (toggle) · White / Dark / AMOLED black themes · 10+ built-in engines + custom URL template (`%s` / `{query}`, properly URL-encoded) · test button · themed icons · no permissions, no ads, no tracking · Japanese & English UI.

See the Japanese sections above for build instructions and project layout.
