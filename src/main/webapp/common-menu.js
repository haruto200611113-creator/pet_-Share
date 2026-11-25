/* =====================================================
   Pet Share
   共通ハンバーガーメニュー
===================================================== */


document.addEventListener(
    "DOMContentLoaded",
    function () {


        /* =================================================
           すでにメニューが存在する場合
        ================================================= */

        if (
            document.querySelector(".side-menu")
        ) {

            initializeMenu();

            positionMenuButton();

            return;
        }


        /* =================================================
           ハンバーガーボタン
        ================================================= */

        const menuButton =
            document.createElement("button");

        menuButton.type =
            "button";

        menuButton.className =
            "menu-button";

        menuButton.id =
            "menuButton";

        menuButton.setAttribute(
            "aria-label",
            "メニューを開く"
        );

        menuButton.innerHTML =
            "☰";


        /* =================================================
           背景オーバーレイ
        ================================================= */

        const menuOverlay =
            document.createElement("div");

        menuOverlay.className =
            "menu-overlay";

        menuOverlay.id =
            "menuOverlay";


        /* =================================================
           サイドメニュー
        ================================================= */

        const sideMenu =
            document.createElement("aside");

        sideMenu.className =
            "side-menu";

        sideMenu.id =
            "sideMenu";


        /* =================================================
           メニュー内容
        ================================================= */

        sideMenu.innerHTML = `

            <div class="side-menu-header">

                <h2 class="side-menu-title">
                    Pet Share
                </h2>


                <button
                    type="button"
                    class="close-menu-button"
                    id="closeMenuButton"
                    aria-label="メニューを閉じる"
                >
                    ×
                </button>

            </div>


            <ul class="side-menu-list">

                <li>
                    <a href="home.html">

                        <span class="side-menu-icon">
                            🏠
                        </span>

                        ホーム

                    </a>
                </li>


                <li>
                    <a href="mypage.jsp">

                        <span class="side-menu-icon">
                            👤
                        </span>

                        マイページ

                    </a>
                </li>


                <li>
                    <a href="profile-edit.html">

                        <span class="side-menu-icon">
                            ✏️
                        </span>

                        プロフィール編集

                    </a>
                </li>


                <li>
                    <a href="settings.html">

                        <span class="side-menu-icon">
                            ⚙️
                        </span>

                        設定

                    </a>
                </li>


                <li>
                    <a href="contact.html">

                        <span class="side-menu-icon">
                            ✉️
                        </span>

                        お問い合わせ

                    </a>
                </li>


                <li>
                    <a href="faq.html">

                        <span class="side-menu-icon">
                            ❓
                        </span>

                        よくある質問

                    </a>
                </li>


                <li>
                    <button
                        type="button"
                        id="logoutButton"
                    >

                        <span class="side-menu-icon">
                            🚪
                        </span>

                        ログアウト

                    </button>
                </li>

            </ul>

        `;


        /* =================================================
           HTMLへ追加
        ================================================= */

        document.body.appendChild(
            menuButton
        );

        document.body.appendChild(
            menuOverlay
        );

        document.body.appendChild(
            sideMenu
        );


        /* =================================================
           ハンバーガーボタンを
           ページタイトルの横へ配置
        ================================================= */

        positionMenuButton();


        /* =================================================
           イベント設定
        ================================================= */

        initializeMenu();

    }
);


/* =====================================================
   ページタイトル＋ハンバーガーを作成
===================================================== */

function positionMenuButton() {

    const menuButton =
        document.getElementById(
            "menuButton"
        );


    if (!menuButton) {
        return;
    }


    /* ---------------------------------------------
       すでに配置済みなら何もしない
    --------------------------------------------- */

    if (
        document.querySelector(
            ".page-title-with-menu"
        )
    ) {

        const existingWrapper =
            document.querySelector(
                ".page-title-with-menu"
            );

        if (
            !existingWrapper.contains(
                menuButton
            )
        ) {

            existingWrapper.appendChild(
                menuButton
            );

        }

        return;
    }


    /* ---------------------------------------------
       ページ内の最初のh1を取得
    --------------------------------------------- */

    const pageTitle =
        document.querySelector(
            "h1"
        );


    /*
     * h1がないページでは何もしない
     */
    if (!pageTitle) {
        return;
    }


    /* ---------------------------------------------
       タイトル用ラッパーを作成
    --------------------------------------------- */

    const wrapper =
        document.createElement(
            "div"
        );

    wrapper.className =
        "page-title-with-menu";


    /* ---------------------------------------------
       h1の親要素を取得
    --------------------------------------------- */

    const parent =
        pageTitle.parentNode;


    /* ---------------------------------------------
       h1の前にラッパーを配置
    --------------------------------------------- */

    parent.insertBefore(
        wrapper,
        pageTitle
    );


    /* ---------------------------------------------
       h1をラッパーへ移動
    --------------------------------------------- */

    wrapper.appendChild(
        pageTitle
    );


    /* ---------------------------------------------
       ハンバーガーをラッパーへ移動
    --------------------------------------------- */

    wrapper.appendChild(
        menuButton
    );

}


/* =====================================================
   メニュー初期化
===================================================== */

function initializeMenu() {

    const menuButton =
        document.getElementById(
            "menuButton"
        );

    const sideMenu =
        document.getElementById(
            "sideMenu"
        );

    const closeMenuButton =
        document.getElementById(
            "closeMenuButton"
        );

    const menuOverlay =
        document.getElementById(
            "menuOverlay"
        );


    /* =================================================
       必要な要素がない場合
    ================================================= */

    if (
        !menuButton ||
        !sideMenu ||
        !closeMenuButton ||
        !menuOverlay
    ) {

        return;

    }


    /* =================================================
       二重登録防止
    ================================================= */

    if (
        menuButton.dataset.menuInitialized ===
        "true"
    ) {

        return;

    }


    menuButton.dataset.menuInitialized =
        "true";


    /* =================================================
       メニューを開く
    ================================================= */

    function openMenu() {

        sideMenu.classList.add(
            "show"
        );

        menuOverlay.classList.add(
            "show"
        );

        document.body.classList.add(
            "menu-open"
        );

        document.body.style.overflow =
            "hidden";

    }


    /* =================================================
       メニューを閉じる
    ================================================= */

    function closeMenu() {

        sideMenu.classList.remove(
            "show"
        );

        menuOverlay.classList.remove(
            "show"
        );

        document.body.classList.remove(
            "menu-open"
        );

        document.body.style.overflow =
            "";

    }


    /* =================================================
       ☰ボタン
    ================================================= */

    menuButton.addEventListener(
        "click",
        function () {

            openMenu();

        }
    );


    /* =================================================
       ×ボタン
    ================================================= */

    closeMenuButton.addEventListener(
        "click",
        function () {

            closeMenu();

        }
    );


    /* =================================================
       背景クリック
    ================================================= */

    menuOverlay.addEventListener(
        "click",
        function () {

            closeMenu();

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
            ) {

                closeMenu();

            }

        }
    );


    /* =================================================
       ログアウト
    ================================================= */

    const logoutButton =
        document.getElementById(
            "logoutButton"
        );


    if (logoutButton) {

        logoutButton.addEventListener(
            "click",
            function () {

                if (
                    typeof window.showLogoutConfirm ===
                    "function"
                ) {

                    window.showLogoutConfirm();

                } else {

                    const result =
                        confirm(
                            "ログアウトしますか？"
                        );


                    if (!result) {
                        return;
                    }


                    sessionStorage.clear();


                    window.location.href =
                        "login.html";

                }

            }
        );

    }

}