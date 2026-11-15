/* =====================================================
   Pet Share Select
   シェア停止確認モーダル
===================================================== */

document.addEventListener(
    "DOMContentLoaded",
    function () {

        /*
         * モーダル
         */
        const modal =
            document.getElementById(
                "share-stop-confirm-modal"
            );


        /*
         * キャンセルボタン
         */
        const cancelButton =
            document.getElementById(
                "share-stop-cancel-button"
            );


        /*
         * 確定ボタン
         */
        const confirmButton =
            document.getElementById(
                "share-stop-confirm-button"
            );


        /*
         * 現在確認中のフォーム
         */
        let selectedForm = null;



        /* =================================================
           シェア停止フォーム
        ================================================= */

        const stopShareForms =
            document.querySelectorAll(
                ".stop-share-form"
            );


        stopShareForms.forEach(
            function (form) {

                form.addEventListener(
                    "submit",
                    function (event) {

                        /*
                         * 通常のsubmitを一旦止める
                         *
                         * ここではまだServletへ送信しない
                         */
                        event.preventDefault();


                        /*
                         * どのペットのフォームか保存
                         */
                        selectedForm = form;


                        /*
                         * モーダルを表示
                         */
                        modal.style.display =
                            "flex";


                        /*
                         * 背景スクロールを停止
                         */
                        document.body.style.overflow =
                            "hidden";

                    }
                );

            }
        );



        /* =================================================
           キャンセル
        ================================================= */

        cancelButton.addEventListener(
            "click",
            function () {

                closeModal();

            }
        );



        /* =================================================
           「シェアをやめる」
        ================================================= */

        confirmButton.addEventListener(
            "click",
            function () {

                if (selectedForm !== null) {

                    /*
                     * 確認済みなので、
                     * 本来のフォーム送信を実行
                     */
                    selectedForm.submit();

                }

            }
        );



        /* =================================================
           モーダル背景をクリック
        ================================================= */

        modal.addEventListener(
            "click",
            function (event) {

                /*
                 * モーダル本体ではなく
                 * 背景をクリックした場合
                 */
                if (
                    event.target === modal
                ) {

                    closeModal();

                }

            }
        );



        /* =================================================
           ESCキー
        ================================================= */

        document.addEventListener(
            "keydown",
            function (event) {

                if (
                    event.key === "Escape"
                    &&
                    modal.style.display !== "none"
                ) {

                    closeModal();

                }

            }
        );



        /* =================================================
           モーダルを閉じる
        ================================================= */

        function closeModal() {

            modal.style.display =
                "none";

            selectedForm = null;

            /*
             * 背景スクロールを元に戻す
             */
            document.body.style.overflow =
                "";

        }

    }
);