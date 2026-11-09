"use strict";

/* =========================================================
 * DBからプロフィール情報を取得
 * ========================================================= */

async function loadProfile() {

    try {

        const response =
            await fetch("profile-get", {
                method: "GET"
            });


        /* -----------------------------------------
         * ログインしていない場合
         * ----------------------------------------- */

        if (response.status === 401) {

            alert("ログインしてください。");

            window.location.href =
                "login.html";

            return;
        }


        /* -----------------------------------------
         * その他のエラー
         * ----------------------------------------- */

        if (!response.ok) {

            throw new Error(
                "プロフィール情報の取得に失敗しました。"
            );
        }


        /* -----------------------------------------
         * JSON取得
         * ----------------------------------------- */

        const profile =
            await response.json();
            console.log("取得したプロフィール:", profile);
console.log("プロフィール画像:", profile.profileImage);
console.log("画像データ長:", profile.profileImage?.length);


        /* -----------------------------------------
         * アカウント名を表示
         * ----------------------------------------- */

        if (accountNameInput) {

            accountNameInput.value =
                profile.accountName || "";

        }


        /* -----------------------------------------
         * 自己紹介を表示
         * ----------------------------------------- */

        if (selfIntroductionInput) {

            selfIntroductionInput.value =
                profile.selfIntroduction || "";

        }


        /* -----------------------------------------
         * プロフィール画像
         * ----------------------------------------- */

        if (
    profileImagePreview
    && profile.profileImage
) {
    profileImagePreview.src =
        profile.profileImage;

    profileImagePreview.style.display = "block";

    const profileImageDefault =
        document.getElementById("profileImageDefault");

    if (profileImageDefault) {
        profileImageDefault.style.display = "none";
    }
}


        console.log(
            "プロフィール情報の読み込み成功"
        );


    } catch (error) {

        console.error(
            "プロフィール取得エラー：",
            error
        );

        alert(
            "プロフィール情報の取得に失敗しました。"
        );
    }
}
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


const cancelButton =
    document.getElementById("cancelButton");

const backButton =
    document.getElementById("backButton");


/* =========================================================
 * 左上の戻るボタン
 * マイページへ戻る
 * ========================================================= */

if (backButton) {

    backButton.addEventListener(
        "click",
        function () {

            window.location.href = "mypage.jsp";

        }
    );
}

/* =========================================================
 * キャンセルボタン
 * マイページへ戻る
 * ========================================================= */

if (cancelButton) {

    cancelButton.addEventListener(
        "click",
        function () {

            window.location.href = "mypage.jsp";

        }
    );
}
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
             * ProfileUpdateServlet.javaの
             * request.getParameter()で受け取れるように
             * URLSearchParamsを使用する。
             * ============================================= */

            const formData =
    new FormData();

formData.append(
    "accountName",
    accountName
);

formData.append(
    "selfIntroduction",
    selfIntroduction
);

/* プロフィール画像 */
if (
    profileImageInput &&
    profileImageInput.files.length > 0
) {

    formData.append(
        "profileImage",
        profileImageInput.files[0]
    );
}


            /* =============================================
             * 保存処理
             * ============================================= */

            try {

                /* -----------------------------------------
                 * 保存中表示
                 * ----------------------------------------- */

                saveProfileButton.disabled =
                    true;

                saveProfileButton.textContent =
                    "保存中...";


                /* -----------------------------------------
                 * Servletへ送信
                 * ----------------------------------------- */

                const response =
    await fetch(
        "profile-update",
        {
            method: "POST",
            body: formData
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
                 * 保存成功
                 * ========================================= */

                if (response.ok) {

                    console.log(
                        "プロフィールの保存に成功しました"
                    );


                    /* -------------------------------------
                     * 変更完了メッセージ
                     *
                     * 他の変更画面と同じ
                     * change-complete-message
                     * を使用する。
                     * ------------------------------------- */

                    const message =
                        document.createElement("div");

                    message.className =
                        "change-complete-message";

                    message.textContent =
                        "変更完了しました";

                    document.body.appendChild(
                        message
                    );


                    /* -------------------------------------
                     * 1.5秒後にマイページへ移動
                     * ------------------------------------- */

                    setTimeout(
                        function () {

                            window.location.href =
                                "mypage.jsp";

                        },
                        1500
                    );

                }

                /* =========================================
                 * 保存失敗
                 * ========================================= */

                else {

                    const errorMessage =
                        await response.text();

                    console.error(
                        "プロフィールの保存に失敗しました"
                    );

                    console.error(
                        "エラーメッセージ：" +
                        errorMessage
                    );


                    alert(
                        "プロフィールの保存に失敗しました。\n" +
                        errorMessage
                    );


                    /* -------------------------------------
                     * 保存ボタンを元に戻す
                     * ------------------------------------- */

                    saveProfileButton.disabled =
                        false;

                    saveProfileButton.textContent =
                        "保存する";
                }

            }

            /* =============================================
             * 通信エラー
             * ============================================= */

            catch (error) {

                console.error(
                    "プロフィール更新エラー：",
                    error
                );

                alert(
                    "プロフィールの保存中にエラーが発生しました。"
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
/* =========================================================
 * ページ読み込み時にプロフィールを取得
 * ========================================================= */

loadProfile();