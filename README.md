## Overview

amazon出品者向けのシステムです。

amazonサーバーから注文情報を取得して購入者のメールアドレスにお礼メールを送信します。



## Description

### １．環境図

![](https://maidomail9publicbucket.s3-ap-northeast-1.amazonaws.com/shin.bmp)

### ２．各処理について

実行するjavaファイルは src/main/java/exec にまとめています



##### 	①レポート要求処理 （RequestReport2H.java）  

​        開始日と終了日を決めてamazonサーバに注文レポートの作成を要求します  amazonサーバからは要求IDが返されます。  

​        運用では2時間毎に定期実行しています。

##### 	②要求ステータス確認処理 （GetReportRequestList.java）  

​        レポート作成が完了したかを確認する処理です  

​        作成完了してた場合はamazonサーバからレポートIDが返されます。

##### 	③レポート取得処理 （GetFbaShipmentReport.java）  

​        amazonサーバから注文レポートを取得します。  

​        amazonから指定した期間内の注文レポートがTSV形式で返されます。 

​        受け取ったTSV形式のバイトストリームを整形し、注文情報としてDB保存します

##### 	④メール作成処理 （CreateThanksMessage.java）

​        注文情報とメールテンプレートをDBから取得して  購入者へ送信するメール情報を作成します。  

​        メール情報は別テーブル（送信メール一覧）に保存します

##### 	⑤メール送信処理 （CreateThanksMessage.java） 

​        送信メール一覧からメール送信します。  

​        毎日19時に送信します

##### 	⑥開封・リンククリックログ取得処理 

​        別プロジェクト参照 [Link](https://github.com/kota-imai/amznmail_statistics_import)



## Demo

購入者にはこのようなメールが送信されます

<img src="https://maidomail9publicbucket.s3-ap-northeast-1.amazonaws.com/maidomail_demo.png" style="zoom:40%;" />



## VS.

[メルゾン](https://mailzon.net/ja/)

開封イベントやクリックイベントの取得可能、メールへの画像挿入、HTML形式のメール送信可能という点で差別化を行った。



## Author

Kota Imai



## License

MIT