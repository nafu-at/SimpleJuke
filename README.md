# SimpleJuke
![GitHub release](https://img.shields.io/github/release/nafu-at/SimpleJuke.svg) ![GitHub release](https://img.shields.io/github/release-pre/nafu-at/SimpleJuke.svg) ![GitHub](https://img.shields.io/github/license/nafu-at/SimpleJuke.svg) [![Build Status](https://travis-ci.com/nafu-at/SimpleJuke.svg?branch=dev)](https://travis-ci.com/nafu-at/SimpleJuke) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/af5ce77daa0a4397a52124b0fd7ef599)](https://www.codacy.com/app/NAFU_at/SimpleJuke?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=nafu-at/SimpleJuke&amp;utm_campaign=Badge_Grade)

SimpleJukeはDiscordユーザー向けの音楽再生用Botです。  
必要な機能に絞りどんなユーザーでも簡単に使うことができることに焦点を当て開発しています。  
パソコンが不自由なく使用でき少しの知識があれば自分でBotをホストすることもできます。

## 動作要項
- [x] Windows, MacOS, Linuxなどの一般的なPC用OS
- [x] Java11以降の実行環境
- [x] MariaDB 5.5以降
- [x] MySQL 5.5以降
- [x] Discordアカウント

## 使用方法
### 公式ホストのBotを利用する場合
こちらのページよりBotの招待リンクをご利用下さい。  
https://pandasoft.dev/service/simplejuke

### 自分でBotをホストする場合
データベースのセットアップは公式のドキュメントを御覧ください。

1. SimpleJukeリポジトリのReleaseから最新のSimpleJuke.jarをダウンロードします。
1. jarファイルを稼働させたいフォルダに配置します。
1. プログラムを起動して設定ファイルを生成します。
1. 生成された設定を開き、必要な認証情報などを入力し保存します。
1. 再度プログラムを起動します。

プログラムが起動しオンラインステータスがオンラインになれば起動完了です。

### プログラムの不具合を発見した場合
SimpleJukeのリポジトリにIssueを立てて報告してください。  
バグ修正・機能追加などのプルリクエストも歓迎しています。  
ソースコードを改変する際はオリジナルのコードスタイルを変更しないよう注意してください。

### ライセンス
このプログラムのオリジナルソースコードはApache License 2.0に基づき公開しています。
```
    Copyright 2019 くまねこそふと.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```

---

### Thaad Party License
#### MIT License
- SLF4J api
- Lavalink-Client

#### The 3-Clause BSD License
- Sentry Java For Logback

#### GNU General Public License, version 2
**(The Universal FOSS Exception, Version 1.0)**
- MySQL Connector/J

#### GNU Lesser General Public License 2.1
- Logback
- MariaDB Connector/J

#### Apache Lisence, Version 2.0
- JDA
- lavaplayer
- OkHttp3
- Jackson Core
- Jackson Databind
- jackson-dataformat-yaml
- Apache Commons Codec
- Apache Commons IO
- Apache Commons Lang
- HikariCP
- SQLite JDBC
