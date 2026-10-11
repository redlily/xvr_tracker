# xvr_tracker

<img src="assets/app_icon.svg" width="128" alt="CamTra icon">

## 概要

<img src="assets/overview.jpg" width="500" alt="CamTra overview">

[English](README.en.md)

AI（MediaPipe Pose Landmarker）を使用してAndroid、iOSアプリ上でカメラを通してユーザーの姿勢を計測・推論し、そのデータをVRChatにフルボディトラッキング（Full Body Tracking）用データとして送信するためのアプリです。

## 開発環境

| ツール | バージョン |
|--------|------------|
| Android SDK | 37 |
| Android Gradle Plugin | 9.4.1 |
| Gradle | 9.6.0 |
| OpenJDK | 21（コンパイル対象。Gradle JDK は 17〜26） |
| Xcode | 16.2 |
| CocoaPods | 1.17.0 |

## サポートバージョン

利用に必要な OS バージョンです。

| プラットフォーム | 対応 OS |
|------------------|----------|
| Android | Android 10 以上 |
| iOS | iOS 18.2 以上 |

## ビルドと実機への転送

ビルドと実機への転送は、**Android Studio** / **Xcode** を使う前提です。

### Android

#### 設定

1. [Android Studio](https://developer.android.com/studio) をインストールする。
2. Android Studio でリポジトリ内の `android/` ディレクトリを開く。
3. SDK Manager で **Android SDK 37**（`compileSdk` / `targetSdk`）を入れる。
4. **Settings → Build Tools → Gradle → Gradle JDK** で **JDK 21** を選ぶ（プロジェクトのコンパイル対象。Gradle 自体は 17〜26 で動作可能）。
5. 初回の Gradle Sync を完了する（`local.properties` は自動生成され、Git 管理対象外）。

- `applicationId`: `com.exmachina.xvrtracker`
- `minSdk`: 29 / `targetSdk`: 37

#### ビルド

1. 上部の実行構成でアプリモジュール（`app`）が選ばれていることを確認する。
2. メニューの **Build → Make Project** を実行する。  
   APK だけ欲しい場合は **Build → Build Bundle(s) / APK(s) → Build APK(s)**。

#### 実機への転送

1. USB で Android 端末を接続し、端末側で **開発者向けオプション** と **USBデバッグ** を有効にする。
2. Android Studio 上部の端末一覧から実機を選ぶ。
3. **Run → Run 'app'**（▶）でビルドして転送・起動する。

### iOS

無料の Apple ID（Personal Team）で、自分の iPhone へ開発用インストールする前提です。有料の Apple Developer Program は不要です。署名はおおよそ **7日間** で切れ、その後は Xcode から再インストールが必要です。

#### 設定

1. まだなら [Apple ID](https://appleid.apple.com/) を作成する。
2. Mac に [Xcode](https://developer.apple.com/xcode/) を入れ、初回起動で追加コンポーネントのインストールを完了する。
3. Xcode → **Settings → Accounts** で Apple ID を追加する。
4. 初回のみ、ターミナルで CocoaPods 依存を入れる（以降のビルド／転送は Xcode 上で行う）。

```bash
cd ios/xvr_tracker
pod install
```

5. **`xvr_tracker.xcworkspace`** を Xcode で開く（`.xcodeproj` ではない）。
6. 左のターゲット一覧からアプリ本体（`xvr_tracker`）を選び、**Signing & Capabilities** で次を設定する。
   - **Automatically manage signing** を ON
   - **Team** に自分の Apple ID（Personal Team）を選択
   - Bundle Identifier が衝突する場合は、自分用に一意な値へ変更する（例: `com.yourname.xvrtracker`）
7. iPhone を USB で接続し、端末側で **このコンピュータを信頼**する。
8. iOS 16 以降は **設定 → プライバシーとセキュリティ → デベロッパモード** を ON にし、再起動する（項目が無い場合は、一度 Xcode から Run を試すと出ることがあります）。

- デプロイメントターゲット: iOS 18.2 前後（Xcode / 端末 OS をそれに合わせてください）

#### ビルド

1. Xcode 上部でスキーム `xvr_tracker` と接続中の実機（またはシミュレータ）を選ぶ。
2. **Product → Build**（`⌘B`）でビルドする。  
   署名エラーが出る場合は、上記の Team / Bundle Identifier を見直す。

#### 実機への転送

1. Xcode 上部で実機を選んだまま **Product → Run**（`⌘R`）する。
2. 初回は iPhone 側で **設定 → 一般 → VPNとデバイス管理**（表記は OS により異なる）を開き、開発元アプリを **信頼**する。
3. ホーム画面に入った CamTra を起動する。
4. 署名期限が切れたら、Xcode から再度 Run して入れ直す。

## 使い方

### PC側の設定

#### VRChatのIPアドレスを調べる

CamTra には、**VRChatを起動しているPCのローカルIPアドレス**（例: `192.168.x.x`）を設定します。スマホとPCは同じローカルネットワーク（同じWi‑Fiなど）に接続してください。

**コマンドプロンプト / PowerShell**

```bat
ipconfig
```

表示されたアダプターのうち、使っているLAN/Wi‑Fiの **IPv4 アドレス** を控えます。

特定のインターフェースだけ見る例:

```powershell
Get-NetIPAddress -AddressFamily IPv4 | Where-Object { $_.IPAddress -notlike '127.*' }
```

GUIから確認する場合:
**設定 → ネットワークとインターネット → プロパティ** の IPv4 アドレス

#### VRChat側の設定

1. PCで **VRChatを起動**する。
2. Expressionメニューを開く。
3. **Option → OSC → Enable** を **ON** にする。
4. メニューを開いて「キャリブレーション」が表示されない場合は、**VRChatを再起動**する。
5. CamTra からの受信ポートは通常 **9000** です（VRChatのデフォルト受信ポート）。

参考: [OSC Overview](https://docs.vrchat.com/docs/osc-overview)

### アプリ側の設定

初回起動時はチュートリアル（オンボーディング）が表示されます。スキップした場合や再度確認したい場合は、**設定 → その他 → チュートリアル** からやり直せます。

1. CamTra（カムトラ）を起動する。
2. **身長**を入力する（ポーズ推定の精度向上のため）。
3. **送信先のIPアドレス**に、VRChatを起動しているPCのIPを入力する（ポートは通常 `9000`）。後から **設定 → VRChatへの接続** でも変更できます。
4. **トラッカー起動**後に、必要な権限を許可する。
   - カメラへのアクセス権限
   - ローカルネットワークへのアクセス権限（特に iOS）
5. 設定画面の **トラッカー起動** を押す。
6. VRChat側でキャリブレーションを行う。
   1. トラッカーを起動した状態にする
   2. メニューを開く
   3. **キャリブレーション**を開く
   4. 表示されるT字ポーズのアバターに合わせ、IKポイントが各部位に合うよう調整する
   5. 両手コントローラーのトリガーを引く

### PC・アプリ間の疎通テスト

アプリからVRChatへトラッキングデータが届いているかを確認します。

1. CamTraで **トラッカーを起動**する。
2. VRChatで Expressionメニューを開く。
3. **Option → OSC → Debug** を選択する。
4. デバッグコンソールに、アプリからのOSCデータが表示されていることを確認する。

## 参照

- [Pose landmark detection guide](https://developers.google.com/edge/mediapipe/solutions/vision/pose_landmarker)
  - AIによるポーズ推定用のフレームワーク
- [OSC Trackers](https://docs.vrchat.com/docs/osc-trackers)
  - VRChatへのボディートラッキングデータ送信に関する仕様
- [VRChat用のフルトラをWEBカメラとMediaPipeで作ってみた](https://qiita.com/as_kuya/items/fd10b3c1156f603604b3)
  - VRChatへのデータ送信に関する調査

## ライセンス

このプロジェクトは [MIT License](LICENSE) の下で公開されています。
