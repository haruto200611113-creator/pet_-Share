document.addEventListener("DOMContentLoaded", function () {

    // ==========================================
    // モーダル取得
    // ==========================================

    const modal =
        document.getElementById("share-stop-confirm-modal");

    const cancelButton =
        document.getElementById("share-stop-cancel-button");

    const confirmButton =
        document.getElementById("share-stop-confirm-button");


    // ==========================================
    // 確認中のフォーム
    // ==========================================

    let selectedForm = null;


    // ==========================================
    // シェア停止フォーム取得
    // ==========================================

    const stopShareForms =
        document.querySelectorAll(".stop-share-form");


    console.log(
        "シェア停止フォーム数：",
        stopShareForms.length
    );


    // ==========================================
    // 「シェアをやめる」ボタン
    // ==========================================

    stopShareForms.forEach(function (form) {

        const stopButton =
            form.querySelector(".stop-share-button");


        if (!stopButton) {
            return;
        }


        stopButton.addEventListener("click", function () {

            console.log("シェア停止確認を表示");


            // 押されたフォームを保存
            selectedForm = form;


            // モーダルを表示
            modal.style.display = "flex";


            // 背景スクロール停止
            document.body.style.overflow = "hidden";

        });

    });


    // ==========================================
    // キャンセル
    // ==========================================

    cancelButton.addEventListener("click", function () {

        closeModal();

    });


    // ==========================================
    // 確認
    // ==========================================

    confirmButton.addEventListener("click", function () {

        if (selectedForm !== null) {

            // フォームを送信
            selectedForm.submit();

        }

    });


    // ==========================================
    // 背景クリック
    // ==========================================

    modal.addEventListener("click", function (event) {

        if (event.target === modal) {

            closeModal();

        }

    });


    // ==========================================
    // ESCキー
    // ==========================================

    document.addEventListener("keydown", function (event) {

        if (
            event.key === "Escape" &&
            modal.style.display !== "none"
        ) {

            closeModal();

        }

    });


    // ==========================================
    // モーダルを閉じる
    // ==========================================

    function closeModal() {

        modal.style.display = "none";

        selectedForm = null;

        document.body.style.overflow = "";

    }

});