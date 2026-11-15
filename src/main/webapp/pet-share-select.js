let selectedForm = null;


// ==========================================
// シェア停止確認モーダルを開く
// ==========================================

function openShareStopModal(button) {

    console.log("シェア停止モーダルを開きます");

    selectedForm = button.closest(".stop-share-form");

    const modal =
        document.getElementById("share-stop-confirm-modal");

    if (!modal) {

        console.error(
            "share-stop-confirm-modal が見つかりません"
        );

        return;
    }

    modal.style.display = "flex";

    document.body.style.overflow = "hidden";
}


// ==========================================
// DOM読み込み後
// ==========================================

document.addEventListener(
    "DOMContentLoaded",
    function () {

        console.log(
            "pet-share-select.js 読み込み完了"
        );


        const modal =
            document.getElementById(
                "share-stop-confirm-modal"
            );

        const cancelButton =
            document.getElementById(
                "share-stop-cancel-button"
            );

        const confirmButton =
            document.getElementById(
                "share-stop-confirm-button"
            );


        // ======================================
        // キャンセル
        // ======================================

        cancelButton.addEventListener(
            "click",
            function () {

                closeShareStopModal();

            }
        );


        // ======================================
        // シェア停止確定
        // ======================================

        confirmButton.addEventListener(
            "click",
            function () {

                if (selectedForm !== null) {

                    selectedForm.submit();

                }

            }
        );


        // ======================================
        // 背景クリック
        // ======================================

        modal.addEventListener(
            "click",
            function (event) {

                if (
                    event.target === modal
                ) {

                    closeShareStopModal();

                }

            }
        );

    }
);


// ==========================================
// モーダルを閉じる
// ==========================================

function closeShareStopModal() {

    const modal =
        document.getElementById(
            "share-stop-confirm-modal"
        );

    if (modal) {

        modal.style.display = "none";

    }

    selectedForm = null;

    document.body.style.overflow = "";
}