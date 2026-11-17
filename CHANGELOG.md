# 変更履歴 / Changelog

## 1.0.1 (2026-11-17)

セキュリティ強化リリース。

- カスタム検索エンジン URL を `https://` のみに制限
- 検索 URL のスキームを実行時にも検証 (`http` / `https` 以外を拒否)
- `LinkProxyActivity` を `ACTION_VIEW` + `http` / `https` に制限
- `android:allowBackup="false"` でバックアップ対象から除外
- `taskAffinity=""` で Android 10 未満の task hijacking を緩和
- Android 11+ のブラウザ可視性のため `<queries>` を追加
- lint の `abortOnError` を有効化
- Gradle wrapper に配布物の SHA-256 を追加し、wrapper を 9.6.0 で再生成
- GitHub Actions を SHA ピン留め + `contents: read` に制限、Dependabot を追加
- About のソースコードリンクを実際のリポジトリに修正

## 1.0.0 (2026-11-17)

初回リリース。

- `ACTION_WEB_SEARCH` / `ACTION_SEND` (text/plain) ハンドラ
- 内蔵検索エンジン 11 種 + カスタム URL テンプレート (`%s` / `{query}`、URL エンコード対応)
- テーマ: システムに従う / ホワイト / ダーク / AMOLED ブラック
- Material You (ダイナミックカラー) トグル (Android 12+)
- 検索テストボタン
- 日本語 / 英語 UI
- テーマアイコン (Android 13+) 対応アダプティブアイコン
