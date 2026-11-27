document.addEventListener("DOMContentLoaded", function () {

    const form =
        document.getElementById("petRegisterForm");

    const registerButton =
        document.getElementById("registerButton");

    const cancelButton =
        document.getElementById("cancelButton");

    const backButton =
        document.getElementById("backButton");

    const petImageInput =
        document.getElementById("petImage");

    const petImagePreview =
        document.getElementById("petImagePreview");

    const petImageDefault =
        document.getElementById("petImageDefault");


    // ==================================================
    // URLパラメータ
    // ==================================================

    const params =
        new URLSearchParams(
            window.location.search
        );

    const success =
        params.get("success");

    const error =
        params.get("error");


    // ==================================================
    // 登録成功
    // ==================================================

    if (success === "true") {

        showRegisterCompleteModal();

        return;

    }


    // ==================================================
    // 登録失敗
    // ==================================================

    if (error === "true") {

        showMessage(
            "ペットの登録に失敗しました。入力内容を確認してください。",
            "error"
        );

    }


    // ==================================================
    // 画像エラー
    // ==================================================

    if (error === "image") {

        showMessage(
            "画像の処理に失敗しました。JPG・JPEG形式の画像を選択してください。",
            "error"
        );

    }


    // ==================================================
    // ペット画像プレビュー
    // ==================================================

    if (petImageInput) {

        petImageInput.addEventListener(
            "change",
            function () {

                const file =
                    this.files[0];

                if (!file) {

                    if (petImagePreview) {

                        petImagePreview.src = "";
                        petImagePreview.style.display = "none";

                    }

                    if (petImageDefault) {

                        petImageDefault.style.display =
                            "inline";

                    }

                    return;
                }


                // --------------------------------------
                // JPG・JPEG確認
                // --------------------------------------

                const fileName =
                    file.name.toLowerCase();

                const isJpeg =
                    file.type === "image/jpeg"
                    ||
                    fileName.endsWith(".jpg")
                    ||
                    fileName.endsWith(".jpeg");


                if (!isJpeg) {

                    showMessage(
                        "JPG・JPEG形式の画像を選択してください。",
                        "error"
                    );

                    this.value = "";

                    return;
                }


                // --------------------------------------
                // ファイルサイズ確認
                // --------------------------------------

                const maxSize =
                    5 * 1024 * 1024;


                if (file.size > maxSize) {

                    showMessage(
                        "画像サイズは5MB以下にしてください。",
                        "error"
                    );

                    this.value = "";

                    return;
                }


                // --------------------------------------
                // プレビュー
                // --------------------------------------

                const reader =
                    new FileReader();


                reader.onload =
                    function (event) {

                        if (petImagePreview) {

                            petImagePreview.src =
                                event.target.result;

                            petImagePreview.style.display =
                                "block";

                        }


                        if (petImageDefault) {

                            petImageDefault.style.display =
                                "none";

                        }

                    };


                reader.readAsDataURL(file);

            }
        );

    }


    // ==================================================
    // 登録ボタン
    // ==================================================

    if (form) {

        form.addEventListener(
            "submit",
            function (event) {

                event.preventDefault();


                // --------------------------------------
                // HTML5バリデーション
                // --------------------------------------

                if (!form.checkValidity()) {

                    form.reportValidity();

                    return;

                }


                // --------------------------------------
                // 登録確認モーダル
                // --------------------------------------

                showRegisterConfirmModal(
                    function () {

                        submitRegisterForm();

                    }
                );

            }
        );

    }


    // ==================================================
    // 登録実行
    // ==================================================

    function submitRegisterForm() {

        if (registerButton) {

            registerButton.disabled =
                true;

            registerButton.textContent =
                "登録中...";

        }


        /*
         * 通常のform送信
         *
         * multipart/form-dataで
         * PetRegisterServletへ送信する。
         */

        form.submit();

    }


    // ==================================================
    // 登録確認モーダル
    // ==================================================

    function showRegisterConfirmModal(
        onConfirm
    ) {

        const overlay =
            document.createElement("div");

        overlay.className =
            "pet-confirm-overlay";


        const modal =
            document.createElement("div");

        modal.className =
            "pet-confirm-modal";


        // --------------------------------------
        // タイトル
        // --------------------------------------

        const title =
            document.createElement("h2");

        title.className =
            "pet-confirm-title";

        title.textContent =
            "ペット登録の確認";


        // --------------------------------------
        // メッセージ
        // --------------------------------------

        const message =
            document.createElement("p");

        message.className =
            "pet-confirm-message";

        message.textContent =
            "この内容でペットを登録しますか？";


        // --------------------------------------
        // ボタンエリア
        // --------------------------------------

        const buttonArea =
            document.createElement("div");

        buttonArea.className =
            "pet-confirm-buttons";


        // --------------------------------------
        // はい
        // --------------------------------------

        const yesButton =
            document.createElement("button");

        yesButton.type =
            "button";

        yesButton.className =
            "pet-confirm-ok";

        yesButton.textContent =
            "はい";


        // --------------------------------------
        // いいえ
        // --------------------------------------

        const noButton =
            document.createElement("button");

        noButton.type =
            "button";

        noButton.className =
            "pet-confirm-cancel";

        noButton.textContent =
            "いいえ";


        // --------------------------------------
        // ボタン順
        // はい → いいえ
        // --------------------------------------

        buttonArea.appendChild(
            yesButton
        );

        buttonArea.appendChild(
            noButton
        );


        // --------------------------------------
        // モーダル構築
        // --------------------------------------

        modal.appendChild(
            title
        );

        modal.appendChild(
            message
        );

        modal.appendChild(
            buttonArea
        );

        overlay.appendChild(
            modal
        );

        document.body.appendChild(
            overlay
        );


        // --------------------------------------
        // はい
        // --------------------------------------

        yesButton.addEventListener(
            "click",
            function () {

                overlay.remove();

                onConfirm();

            }
        );


        // --------------------------------------
        // いいえ
        // --------------------------------------

        noButton.addEventListener(
            "click",
            function () {

                overlay.remove();

            }
        );


        // --------------------------------------
        // 背景クリック
        // --------------------------------------

        overlay.addEventListener(
            "click",
            function (event) {

                if (
                    event.target === overlay
                ) {

                    overlay.remove();

                }

            }
        );

    }


    // ==================================================
    // 登録完了モーダル
    // ==================================================

    function showRegisterCompleteModal() {

        const overlay =
            document.createElement("div");

        overlay.className =
            "pet-confirm-overlay";


        const modal =
            document.createElement("div");

        modal.className =
            "pet-confirm-modal";


        // --------------------------------------
        // タイトル
        // --------------------------------------

        const title =
            document.createElement("h2");

        title.className =
            "pet-confirm-title";

        title.textContent =
            "ペット登録完了";


        // --------------------------------------
        // メッセージ
        // --------------------------------------

        const message =
            document.createElement("p");

        message.className =
            "pet-confirm-message";

        message.textContent =
            "登録が完了しました。";


        // --------------------------------------
        // モーダル構築
        // --------------------------------------

        modal.appendChild(
            title
        );

        modal.appendChild(
            message
        );

        overlay.appendChild(
            modal
        );

        document.body.appendChild(
            overlay
        );


        // --------------------------------------
        // 2秒後にホームへ
        // --------------------------------------

        setTimeout(
            function () {

                window.location.href =
                    "home.html";

            },
            2000
        );

    }


    // ==================================================
    // メッセージ表示
    // ==================================================

    function showMessage(
        message,
        type
    ) {

        const overlay =
            document.createElement("div");

        overlay.className =
            "pet-message-overlay";


        const box =
            document.createElement("div");

        box.className =
            "pet-message-box";


        if (type === "error") {

            box.classList.add(
                "pet-message-error"
            );

        }


        const text =
            document.createElement("p");

        text.textContent =
            message;


        box.appendChild(
            text
        );

        overlay.appendChild(
            box
        );

        document.body.appendChild(
            overlay
        );


        setTimeout(
            function () {

                overlay.remove();

            },
            2500
        );

    }


    // ==================================================
    // キャンセル
    // ==================================================

    if (cancelButton) {

        cancelButton.addEventListener(
            "click",
            function () {

                showCancelConfirmModal();

            }
        );

    }


    // ==================================================
    // キャンセル確認
    // ==================================================

    function showCancelConfirmModal() {

        const overlay =
            document.createElement("div");

        overlay.className =
            "pet-confirm-overlay";


        const modal =
            document.createElement("div");

        modal.className =
            "pet-confirm-modal";


        const title =
            document.createElement("h2");

        title.className =
            "pet-confirm-title";

        title.textContent =
            "登録のキャンセル";


        const message =
            document.createElement("p");

        message.className =
            "pet-confirm-message";

        message.textContent =
            "ペット登録をキャンセルしますか？";


        const buttonArea =
            document.createElement("div");

        buttonArea.className =
            "pet-confirm-buttons";


        const yesButton =
            document.createElement("button");

        yesButton.type =
            "button";

        yesButton.className =
            "pet-confirm-ok";

        yesButton.textContent =
            "はい";


        const noButton =
            document.createElement("button");

        noButton.type =
            "button";

        noButton.className =
            "pet-confirm-cancel";

        noButton.textContent =
            "いいえ";


        // はい → いいえ
        buttonArea.appendChild(
            yesButton
        );

        buttonArea.appendChild(
            noButton
        );


        modal.appendChild(
            title
        );

        modal.appendChild(
            message
        );

        modal.appendChild(
            buttonArea
        );

        overlay.appendChild(
            modal
        );

        document.body.appendChild(
            overlay
        );


        // --------------------------------------
        // はい
        // --------------------------------------

        yesButton.addEventListener(
            "click",
            function () {

                overlay.remove();

                window.location.href =
                    "home.html";

            }
        );


        // --------------------------------------
        // いいえ
        // --------------------------------------

        noButton.addEventListener(
            "click",
            function () {

                overlay.remove();

            }
        );

    }


    // ==================================================
    // 戻るボタン
    // ==================================================

    if (backButton) {

        backButton.addEventListener(
            "click",
            function () {

                history.back();

            }
        );

    }

});