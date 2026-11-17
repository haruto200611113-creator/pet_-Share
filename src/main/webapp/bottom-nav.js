/* =====================================================
   Pet Share
   共通下部ナビゲーション
===================================================== */

document.addEventListener(
    "DOMContentLoaded",
    function () {

        /* =================================================
           すでにナビゲーションがある場合は作らない
        ================================================= */

        if (
            document.querySelector(".bottom-nav")
        ) {

            return;

        }


        /* =================================================
           下部ナビゲーションを作成
        ================================================= */

        const nav =
            document.createElement("nav");

        nav.className =
            "bottom-nav";


        /* =================================================
           ナビゲーションHTML
        ================================================= */

        nav.innerHTML = `

            <!-- =========================
                 ホーム
            ========================== -->

            <a
                href="home.html"
                class="nav-item"
                data-page="home"
                aria-label="ホーム"
            >

                <span class="nav-icon">
                    🏠
                </span>

                <small>
                    ホーム
                </small>

            </a>


            <!-- =========================
                 いいね
            ========================== -->

            <a
                href="likes.html"
                class="nav-item"
                data-page="like"
                aria-label="いいね"
            >

                <span class="nav-icon">
                    ♡
                </span>

                <small>
                    いいね
                </small>

            </a>


            <!-- =========================
                 チャット
            ========================== -->

            <a
                href="chat.html"
                class="nav-item"
                data-page="chat"
                aria-label="チャット"
            >

                <span class="nav-icon">
                    💬
                </span>

                <small>
                    チャット
                </small>

            </a>


            <!-- =========================
                 アカウント
            ========================== -->

            <a
                href="mypage.jsp"
                class="nav-item"
                data-page="account"
                aria-label="アカウント"
            >

                <span class="nav-icon">
                    👤
                </span>

                <small>
                    アカウント
                </small>

            </a>

        `;


        /* =================================================
           bodyの最後に追加
        ================================================= */

        document.body.appendChild(nav);


        /* =================================================
           現在のページを判定
        ================================================= */

        const currentPath =
            window.location.pathname;


        const currentFile =
            currentPath.substring(
                currentPath.lastIndexOf("/") + 1
            );


        /* =================================================
           ホーム
        ================================================= */

        if (
            currentFile === "home.html"
            || currentFile === ""
        ) {

            setActive("home");

        }


        /* =================================================
           いいね
        ================================================= */

        else if (
            currentFile === "likes.html"
        ) {

            setActive("like");

        }


        /* =================================================
           チャット
        ================================================= */

        else if (
            currentFile === "chat.html"
        ) {

            setActive("chat");

        }


        /* =================================================
           アカウント
        ================================================= */

        else if (
            currentFile === "mypage.jsp"
        ) {

            setActive("account");

        }

    }
);


/* =====================================================
   アクティブ状態を設定
===================================================== */

function setActive(page) {

    const navItems =
        document.querySelectorAll(
            ".bottom-nav .nav-item"
        );


    navItems.forEach(
        function (item) {

            if (
                item.dataset.page === page
            ) {

                item.classList.add(
                    "active"
                );

            } else {

                item.classList.remove(
                    "active"
                );

            }

        }
    );
}