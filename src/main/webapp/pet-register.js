document.addEventListener("DOMContentLoaded", () => {

    const form = document.getElementById("petRegisterForm");
    const registerButton = document.getElementById("registerButton");

    const petImageInput = document.getElementById("petImage");
    const petImagePreview = document.getElementById("petImagePreview");
    const petImageDefault = document.getElementById("petImageDefault");

    const cancelButton = document.getElementById("cancelButton");
    const backButton = document.getElementById("backButton");


    /*
     * ==========================================
     * ペット画像プレビュー
     * ==========================================
     */
    if (petImageInput) {

        petImageInput.addEventListener("change", function () {

            const file = this.files[0];

            if (!file) {
                return;
            }

            // 画像ファイルか確認
            if (!file.type.startsWith("image/")) {
                alert("画像ファイルを選択してください。");
                this.value = "";
                return;
            }

            // 画像サイズ確認
            const maxSize = 10 * 1024 * 1024; // 10MB

            if (file.size > maxSize) {
                alert("画像サイズは10MB以下にしてください。");
                this.value = "";
                return;
            }

            const reader = new FileReader();

            reader.onload = function (event) {

                if (petImagePreview) {
                    petImagePreview.src = event.target.result;
                    petImagePreview.style.display = "block";
                }

                if (petImageDefault) {
                    petImageDefault.style.display = "none";
                }
            };

            reader.readAsDataURL(file);
        });
    }


    /*
     * ==========================================
     * 登録処理
     * ==========================================
     *
     * formのsubmitは通常のHTML送信に任せる。
     * これにより、画像を含めた全項目を
     * multipart/form-dataでServletへ送信できる。
     */
    if (form) {

        form.addEventListener("submit", function (event) {

            /*
             * ペット名チェック
             */
            const petNameInput =
                document.querySelector('[name="petName"]');

            if (petNameInput &&
                petNameInput.value.trim() === "") {

                event.preventDefault();

                alert("ペットの名前を入力してください。");
                petNameInput.focus();

                return;
            }


            /*
             * ペット種類チェック
             */
            const petTypeInput =
                document.querySelector('[name="petType"]');

            if (petTypeInput &&
                petTypeInput.value.trim() === "") {

                event.preventDefault();

                alert("ペットの種類を選択してください。");
                petTypeInput.focus();

                return;
            }


            /*
             * 最終確認
             */
            const result = confirm(
                "この内容でペットを登録しますか？"
            );

            if (!result) {
                event.preventDefault();
                return;
            }

            /*
             * 二重送信防止
             */
            if (registerButton) {
                registerButton.disabled = true;
                registerButton.textContent = "登録中...";
            }

            /*
             * ここではevent.preventDefault()しない。
             *
             * ↓
             * 通常のform送信
             *
             * ↓
             * /pet-register
             *
             * ↓
             * PetRegisterServlet
             *
             * ↓
             * DBへ登録
             */
        });
    }


    /*
     * ==========================================
     * キャンセルボタン
     * ==========================================
     */
    if (cancelButton) {

        cancelButton.addEventListener("click", () => {

            const result = confirm(
                "ペット登録をキャンセルしますか？"
            );

            if (result) {
                history.back();
            }
        });
    }


    /*
     * ==========================================
     * 戻るボタン
     * ==========================================
     */
    if (backButton) {

        backButton.addEventListener("click", () => {
            history.back();
        });
    }


    /*
     * ==========================================
     * 登録失敗メッセージ
     * ==========================================
     */
    const params = new URLSearchParams(window.location.search);

    if (params.get("error") === "true") {

        alert(
            "ペットの登録に失敗しました。\n" +
            "入力内容を確認して、もう一度お試しください。"
        );
    }

});