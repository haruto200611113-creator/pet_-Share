"use strict";


/* =========================================================
 * HTML要素の取得
 * ========================================================= */

const profileImageInput =
    document.getElementById("profileImageInput");

const profileImagePreview =
    document.getElementById("profileImagePreview");

const accountNameInput =
    document.getElementById("accountName");

const selfIntroductionInput =
    document.getElementById("selfIntroduction");

const saveProfileButton =
    document.getElementById("saveProfileButton");


/* =========================================================
 * プロフィール画像のプレビュー
 * ========================================================= */

if (profileImageInput && profileImagePreview) {

    profileImageInput.addEventListener(
        "change",
        function () {

            const file =
                profileImageInput.files[0];

            if (!file) {
                return;
            }


            /* ---------------------------------------------
             * 画像ファイルか確認
             * --------------------------------------------- */

            if (!file.type.startsWith("image/")) {

                alert(
                    "画像ファイルを選択してください。"
                );

                profileImageInput.value = "";

                return;
            }


            /* ---------------------------------------------
             * ファイルサイズ確認
             * 5MBまで
             * --------------------------------------------- */

            if (file.size > 5 * 1024 * 1024) {

                alert(
                    "画像サイズは5MB以下にしてください。"
                );

                profileImageInput.value = "";

                return;
            }


            /* ---------------------------------------------
             * プレビュー表示
             * --------------------------------------------- */

            const reader =
                new FileReader();

            reader.onload =
                function (event) {

                    profileImagePreview.src =
                        event.target.result;

                };

            reader.readAsDataURL(file);
        }
    );
}


/* =========================================================
 * 自己紹介文の文字数チェック
 * ========================================================= */

if (selfIntroductionInput) {

    selfIntroductionInput.addEventListener(
        "input",
        function () {

            if (
                selfIntroductionInput.value.length > 300
            ) {

                selfIntroductionInput.value =
                    selfIntroductionInput.value.substring(
                        0,
                        300
                    );

            }
        }
    );
}


/* =========================================================
 * 保存ボタン
 * ========================================================= */

if (saveProfileButton) {

    saveProfileButton.addEventListener(
        "click",
        async function () {

            /* =============================================
             * 入力値を取得
             * ============================================= */

            const accountName =
                accountNameInput.value.trim();

            const selfIntroduction =
                selfIntroductionInput.value.trim();


            /* =============================================
             * アカウント名チェック
             * ============================================= */

            if (accountName === "") {

                alert(
                    "アカウント名を入力してください。"
                );

                accountNameInput.focus();

                return;
            }


            /* =============================================
             * アカウント名の文字数チェック
             * ============================================= */

            if (accountName.length > 50) {

                alert(
                    "アカウント名は50文字以内で入力してください。"
                );

                accountNameInput.focus();

                return;
            }


            /* =============================================
             * 自己紹介文チェック
             * ============================================= */

            if (
                selfIntroduction.length > 300
            ) {

                alert(
                    "自己紹介文は300文字以内で入力してください。"
                );

                selfIntroductionInput.focus();

                return;
            }


            /* =============================================
             * 送信データ作成
             *
             * ProfileUpdateServlet.java 側では
             *
             * request.getParameter("accountName")
             * request.getParameter("selfIntroduction")
             *
             * を使用しているため、
             * FormDataではなく
             * application/x-www-form-urlencoded
             * で送信する。
             * ============================================= */

            const params =
                new URLSearchParams();

            params.append(
                "accountName",
                accountName
            );

            params.append(
                "selfIntroduction",
                selfIntroduction
            );


            /* =============================================
             * プロフィール画像について
             *
             * 現在は画像を画面上でプレビューする
             * ところまで実装済み。
             *
             * DBへの画像保存はまだ行わない。
             * ============================================= */


            /* =============================================
             * 保存処理
             * ============================================= */

            try {

                /* -----------------------------------------
                 * 保存ボタンを無効化
                 * ----------------------------------------- */

                saveProfileButton.disabled =
                    true;

                saveProfileButton.textContent =
                    "保存中...";


                /* -----------------------------------------
                 * サーバーへ送信
                 * ----------------------------------------- */

                const response =
                    await fetch(
                        "profile-update",
                        {
                            method: "POST",

                            headers: {
                                "Content-Type":
                                    "application/x-www-form-urlencoded; charset=UTF-8"
                            },

                            body:
                                params.toString()
                        }
                    );


                /* =========================================
                 * ログイン画面へ飛ばされた場合
                 * ========================================= */

                if (
                    response.redirected
                    && response.url.includes("login.html")
                ) {

                    alert(
                        "ログインしてください。"
                    );

                    window.location.href =
                        "login.html";

                    return;
                }


                /* =========================================
                 * HTTPエラー確認
                 * ========================================= */

                if (!response.ok) {

                    throw new Error(
                        "プロフィールの保存に失敗しました。"
                    );
                }


                /* =========================================
                 * 保存成功
                 * ========================================= */

                const message =
                    document.createElement("div");

                message.className =
                    "change-complete-message";

                message.textContent =
                    "プロフィールを保存しました";

                document.body.appendChild(
                    message
                );


                /* =========================================
                 * 1.5秒後にマイページへ移動
                 * ========================================= */

                setTimeout(
                    function () {

                        window.location.href =
                            "mypage.jsp";

                    },
                    1500
                );


            } catch (error) {

                /* =========================================
                 * 保存失敗
                 * ========================================= */

                console.error(
                    "プロフィール保存エラー：",
                    error
                );

                alert(
                    "プロフィールの保存に失敗しました。"
                );


                /* -----------------------------------------
                 * 保存ボタンを元に戻す
                 * ----------------------------------------- */

                saveProfileButton.disabled =
                    false;

                saveProfileButton.textContent =
                    "保存する";
            }
        }
    );
}