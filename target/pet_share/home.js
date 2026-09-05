/* =====================================================
   ペットシェアアプリ
   home.js

   現在：
   JavaScriptの仮データを使用

   将来：
   Java API → MySQLから取得
===================================================== */


/* =====================================================
   現在ログインしているユーザー
===================================================== */

const currentUser = {
    userId: 1
};


/* =====================================================
   仮ペットデータ
   将来的にはJava APIから取得する
===================================================== */

let pets = [
    {
        petId: 1,
        userId: 2,
        name: "ポチ",
        type: "犬",
        age: 3,
        area: "山口県",
        imagePath:
            "https://images.unsplash.com/photo-1552053831-71594a27632d?w=600",
        createdAt:
            "2026-08-30T10:00:00",
        liked: false
    },

    {
        petId: 2,
        userId: 3,
        name: "ミケ",
        type: "猫",
        age: 2,
        area: "福岡県",
        imagePath:
            "https://images.unsplash.com/photo-1518791841217-8f162f1e1131?w=600",
        createdAt:
            "2026-08-29T10:00:00",
        liked: false
    },

    {
        petId: 3,
        userId: 4,
        name: "ココ",
        type: "犬",
        age: 5,
        area: "広島県",
        imagePath:
            "https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=600",
        createdAt:
            "2026-08-28T10:00:00",
        liked: false
    },

    {
        petId: 4,
        userId: 5,
        name: "モモ",
        type: "猫",
        age: 1,
        area: "大阪府",
        imagePath:
            "https://images.unsplash.com/photo-1573865526739-10659fec78a5?w=600",
        createdAt:
            "2026-08-27T10:00:00",
        liked: false
    }
];


/* =====================================================
   HTML要素
===================================================== */

const petList =
    document.getElementById("petList");

const resultCount =
    document.getElementById("resultCount");

const searchInput =
    document.getElementById("searchInput");

const searchButton =
    document.getElementById("searchButton");

const conditionButton =
    document.getElementById("conditionButton");

const filterButton =
    document.getElementById("filterButton");

const sortButton =
    document.getElementById("sortButton");

const conditionArea =
    document.getElementById("conditionArea");

const typeFilter =
    document.getElementById("typeFilter");

const areaFilter =
    document.getElementById("areaFilter");

const sortSelect =
    document.getElementById("sortSelect");

const applyButton =
    document.getElementById("applyButton");


/* =====================================================
   ペット一覧取得
===================================================== */

function loadPets() {

    displayPets(
        getFilteredPets()
    );
}


/* =====================================================
   ペット表示
===================================================== */

function displayPets(list) {

    petList.innerHTML = "";

    resultCount.textContent =
        `${list.length} 件`;


    if (list.length === 0) {

        petList.innerHTML = `
            <p style="
                grid-column: 1 / -1;
                text-align: center;
                color: #998880;
                padding: 40px 0;
            ">
                条件に合うペットが見つかりませんでした。
            </p>
        `;

        return;
    }


    list.forEach(function (pet) {

        const card =
            document.createElement("article");

        card.className =
            "pet-card";


        card.innerHTML = `
            <div class="pet-image-area">

                <a
                    href="pet-detail.html?petId=${pet.petId}"
                    class="pet-image-link"
                >

                    <img
                        class="pet-image"
                        src="${escapeHtml(pet.imagePath)}"
                        alt="${escapeHtml(pet.name)}"
                    >

                </a>


                <button
                    type="button"
                    class="like-button ${pet.liked ? "liked" : ""}"
                    data-pet-id="${pet.petId}"
                    aria-label="いいね"
                >
                    ${pet.liked ? "♥" : "♡"}
                </button>

            </div>


            <div class="pet-info">

                <h3 class="pet-name">
                    ${escapeHtml(pet.name)}
                </h3>


                <p class="pet-detail">
                    種類：${escapeHtml(pet.type)}
                </p>


                <p class="pet-detail">
                    年齢：${pet.age}歳
                </p>


                <p class="pet-detail">
                    📍 ${escapeHtml(pet.area)}
                </p>

            </div>
        `;


        petList.appendChild(card);

    });


    /* =================================================
       いいねボタン
    ================================================= */

    document
        .querySelectorAll(".like-button")
        .forEach(function (button) {

            button.addEventListener(
                "click",
                function (event) {

                    event.preventDefault();

                    event.stopPropagation();


                    const petId =
                        Number(
                            this.dataset.petId
                        );


                    toggleLike(petId);

                }
            );

        });
}


/* =====================================================
   HTMLエスケープ
===================================================== */

function escapeHtml(value) {

    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}


/* =====================================================
   検索・絞り込み
===================================================== */

function getFilteredPets() {

    const keyword =
        searchInput.value
            .trim()
            .toLowerCase();


    const type =
        typeFilter.value;


    const area =
        areaFilter.value;


    const sort =
        sortSelect.value;


    let result =
        pets.filter(function (pet) {

            const keywordMatch =
                !keyword ||
                pet.name
                    .toLowerCase()
                    .includes(keyword) ||
                pet.type
                    .toLowerCase()
                    .includes(keyword) ||
                pet.area
                    .toLowerCase()
                    .includes(keyword);


            const typeMatch =
                !type ||
                pet.type === type;


            const areaMatch =
                !area ||
                pet.area === area;


            return (
                keywordMatch &&
                typeMatch &&
                areaMatch
            );

        });


    /* =================================================
       並び替え
    ================================================= */

    if (sort === "new") {

        result.sort(
            function (a, b) {

                return (
                    new Date(b.createdAt) -
                    new Date(a.createdAt)
                );

            }
        );

    }


    else if (sort === "old") {

        result.sort(
            function (a, b) {

                return (
                    new Date(a.createdAt) -
                    new Date(b.createdAt)
                );

            }
        );

    }


    else if (sort === "name") {

        result.sort(
            function (a, b) {

                return a.name.localeCompare(
                    b.name,
                    "ja"
                );

            }
        );

    }


    return result;
}


/* =====================================================
   検索ボタン
===================================================== */

searchButton.addEventListener(
    "click",
    function () {

        displayPets(
            getFilteredPets()
        );

    }
);


/* =====================================================
   Enterキーで検索
===================================================== */

searchInput.addEventListener(
    "keydown",
    function (event) {

        if (event.key === "Enter") {

            displayPets(
                getFilteredPets()
            );

        }

    }
);


/* =====================================================
   条件検索
===================================================== */

conditionButton.addEventListener(
    "click",
    function () {

        conditionArea.classList.toggle(
            "show"
        );

    }
);


/* =====================================================
   絞り込み
===================================================== */

filterButton.addEventListener(
    "click",
    function () {

        conditionArea.classList.add(
            "show"
        );

    }
);


/* =====================================================
   並び替え
===================================================== */

sortButton.addEventListener(
    "click",
    function () {

        conditionArea.classList.add(
            "show"
        );


        sortSelect.focus();

    }
);


/* =====================================================
   条件適用
===================================================== */

applyButton.addEventListener(
    "click",
    function () {

        displayPets(
            getFilteredPets()
        );

    }
);


/* =====================================================
   いいね
===================================================== */

function toggleLike(petId) {

    const pet =
        pets.find(
            function (item) {

                return item.petId === petId;

            }
        );


    if (!pet) {
        return;
    }


    pet.liked =
        !pet.liked;


    displayPets(
        getFilteredPets()
    );
}


/* =====================================================
   投稿モーダル
===================================================== */

const postButton =
    document.getElementById("postButton");

const postModal =
    document.getElementById("postModal");

const closeModal =
    document.getElementById("closeModal");


postButton.addEventListener(
    "click",
    function () {

        postModal.classList.add(
            "show"
        );

    }
);


closeModal.addEventListener(
    "click",
    function () {

        closePostModal();

    }
);


postModal.addEventListener(
    "click",
    function (event) {

        if (event.target === postModal) {

            closePostModal();

        }

    }
);


function closePostModal() {

    postModal.classList.remove(
        "show"
    );
}


/* =====================================================
   画像プレビュー
===================================================== */

const petImage =
    document.getElementById("petImage");

const imagePreview =
    document.getElementById("imagePreview");


petImage.addEventListener(
    "change",
    function () {

        const file =
            this.files[0];


        if (!file) {

            imagePreview.src = "";

            imagePreview.classList.remove(
                "show"
            );

            return;
        }


        const reader =
            new FileReader();


        reader.onload =
            function (event) {

                imagePreview.src =
                    event.target.result;


                imagePreview.classList.add(
                    "show"
                );

            };


        reader.readAsDataURL(file);

    }
);


/* =====================================================
   ペット投稿
===================================================== */

const postForm =
    document.getElementById("postForm");


postForm.addEventListener(
    "submit",
    function (event) {

        event.preventDefault();


        const imageFile =
            document
                .getElementById("petImage")
                .files[0];


        const name =
            document
                .getElementById("petName")
                .value
                .trim();


        const type =
            document
                .getElementById("petType")
                .value;


        const age =
            document
                .getElementById("petAge")
                .value;


        const area =
            document
                .getElementById("petArea")
                .value;


        if (!imageFile) {

            alert(
                "ペット画像を選択してください。"
            );

            return;
        }


        const reader =
            new FileReader();


        reader.onload =
            function (event) {

                const newPet = {

                    petId:
                        Date.now(),

                    userId:
                        currentUser.userId,

                    name:
                        name,

                    type:
                        type,

                    age:
                        Number(age),

                    area:
                        area,

                    imagePath:
                        event.target.result,

                    createdAt:
                        new Date().toISOString(),

                    liked:
                        false

                };


                pets.unshift(
                    newPet
                );


                postForm.reset();


                imagePreview.src = "";

                imagePreview.classList.remove(
                    "show"
                );


                closePostModal();


                displayPets(
                    getFilteredPets()
                );


                alert(
                    "ペットを投稿しました！"
                );

            };


        reader.readAsDataURL(
            imageFile
        );

    }
);


/* =====================================================
   下部ナビゲーション
===================================================== */

document
    .querySelectorAll(".nav-item")
    .forEach(
        function (item) {

            item.addEventListener(
                "click",
                function () {

                    const page =
                        this.dataset.page;


                    /* =========================
                       ホーム
                    ========================== */

                    if (page === "home") {

                        displayPets(
                            getFilteredPets()
                        );


                        window.scrollTo({

                            top: 0,

                            behavior: "smooth"

                        });

                    }


                    /* =========================
                       いいね
                    ========================== */

                    if (page === "like") {

                        const likedPets =
                            pets.filter(
                                function (pet) {

                                    return pet.liked;

                                }
                            );


                        displayPets(
                            likedPets
                        );

                    }


                    /* =========================
                       チャット
                    ========================== */

                    if (page === "chat") {

                        alert(
                            "チャット機能はJavaとMySQLを接続した後に実装できます。"
                        );

                    }


                    /* =========================
                       アカウント
                    ========================== */

                    if (page === "account") {

                        alert(
                            "アカウント画面はこれから実装できます。"
                        );

                    }


                    /* =========================
                       active変更
                    ========================== */

                    document
                        .querySelectorAll(".nav-item")
                        .forEach(
                            function (nav) {

                                nav.classList.remove(
                                    "active"
                                );

                            }
                        );


                    this.classList.add(
                        "active"
                    );

                }
            );

        }
    );


/* =====================================================
   ハンバーガーメニュー
===================================================== */

const menuButton =
    document.getElementById("menuButton");

const sideMenu =
    document.getElementById("sideMenu");

const closeMenuButton =
    document.getElementById("closeMenuButton");

const menuOverlay =
    document.getElementById("menuOverlay");


/* =====================================================
   メニューを開く
===================================================== */

function openMenu() {

    sideMenu.classList.add(
        "show"
    );


    menuOverlay.classList.add(
        "show"
    );


    document.body.style.overflow =
        "hidden";
}


/* =====================================================
   メニューを閉じる
===================================================== */

function closeMenu() {

    sideMenu.classList.remove(
        "show"
    );


    menuOverlay.classList.remove(
        "show"
    );


    document.body.style.overflow =
        "";
}


/* =====================================================
   ☰ボタン
===================================================== */

menuButton.addEventListener(
    "click",
    function () {

        openMenu();

    }
);


/* =====================================================
   ×ボタン
===================================================== */

closeMenuButton.addEventListener(
    "click",
    function () {

        closeMenu();

    }
);


/* =====================================================
   背景をクリックして閉じる
===================================================== */

menuOverlay.addEventListener(
    "click",
    function () {

        closeMenu();

    }
);


/* =====================================================
   ESCキーで閉じる
===================================================== */

document.addEventListener(
    "keydown",
    function (event) {

        if (event.key === "Escape") {

            closeMenu();

        }

    }
);


/* =====================================================
   ログアウト
===================================================== */

const logoutButton =
    document.getElementById("logoutButton");


logoutButton.addEventListener(
    "click",
    function () {

        const result =
            confirm(
                "ログアウトしますか？"
            );


        if (!result) {

            return;

        }


        /*
         * Javaと接続した後は、
         * セッションを破棄して
         * ログイン画面へ移動します。
         */


        alert(
            "ログアウトしました。"
        );


        window.location.href =
            "login.html";

    }
);


/* =====================================================
   初期表示
===================================================== */

loadPets();