// ========================================
// ペット詳細ページ
// ========================================


// ========================================
// ペットID取得
// ========================================

const params =
    new URLSearchParams(window.location.search);

const petId =
    params.get("petId");


// ========================================
// ペット情報取得
// ========================================

async function loadPetDetail() {

    // petIdがない場合
    if (!petId) {

        alert("ペットIDが指定されていません。");

        window.location.href =
            "home.html";

        return;
    }


    try {

        const response =
            await fetch(
                `pet-detail?petId=${encodeURIComponent(petId)}`
            );


        if (!response.ok) {

            throw new Error(
                "ペット情報の取得に失敗しました。"
            );

        }


        const pet =
            await response.json();


        console.log(
            "取得したペット情報:",
            pet
        );


        displayPetDetail(pet);


    } catch (error) {

        console.error(
            "ペット詳細取得エラー:",
            error
        );

        alert(
            "ペット情報を取得できませんでした。"
        );

        window.location.href =
            "home.html";

    }

}


// ========================================
// ペット情報表示
// ========================================

function displayPetDetail(pet) {


    // ========================================
    // ペット名
    // ========================================

    const petName =
        document.getElementById("petName");


    if (petName) {

        petName.textContent =
            pet.name || "名前未設定";

    }



    // ========================================
    // ペット画像
    // ========================================

    const petImage =
        document.getElementById("petImage");


    if (petImage) {

        if (pet.imagePath) {

            petImage.src =
                pet.imagePath;

        } else {

            petImage.src =
                "https://placehold.co/800x600?text=Pet+Image";

        }


        petImage.alt =
            pet.name || "ペット画像";

    }



    // ========================================
    // ペット種類
    // ========================================

    const petType =
        document.getElementById("petType");


    if (petType) {

        let typeText =
            pet.type || "未設定";


        if (pet.breed) {

            typeText +=
                `（${pet.breed}）`;

        }


        petType.textContent =
            typeText;

    }



    // ========================================
    // 年齢
    // ========================================

    const petAge =
        document.getElementById("petAge");


    if (petAge) {

        if (pet.age !== undefined &&
            pet.age !== null) {

            petAge.textContent =
                `${pet.age}歳`;

        } else {

            petAge.textContent =
                "不明";

        }

    }



    // ========================================
    // 料金
    // ========================================

    const petPrice =
        document.getElementById("petPrice");


    if (petPrice) {

        if (
            pet.sharePrice !== undefined &&
            pet.sharePrice !== null
        ) {

            petPrice.textContent =
                Number(
                    pet.sharePrice
                ).toLocaleString();

        } else {

            petPrice.textContent =
                "0";

        }

    }



    // ========================================
    // シェア可能期間
    // ========================================

    const sharePeriod =
        document.getElementById(
            "sharePeriod"
        );


    if (sharePeriod) {

        const startDate =
            formatDate(
                pet.shareStartDate
            );

        const endDate =
            formatDate(
                pet.shareEndDate
            );


        if (startDate && endDate) {

            sharePeriod.textContent =
                `${startDate} ～ ${endDate}`;

        } else if (startDate) {

            sharePeriod.textContent =
                `${startDate} ～`;

        } else if (endDate) {

            sharePeriod.textContent =
                `～ ${endDate}`;

        } else {

            sharePeriod.textContent =
                "設定なし";

        }

    }



    // ========================================
    // ペットについて
    // ========================================

    const petDescription =
        document.getElementById(
            "petDescription"
        );


    if (petDescription) {

        let description = "";


        // 性格

        if (pet.personality) {

            description +=
                `性格：${pet.personality}`;

        }


        // 特徴

        if (pet.features) {

            if (description) {

                description +=
                    "\n";

            }

            description +=
                `特徴：${pet.features}`;

        }


        // 好きなもの

        if (pet.likes) {

            if (description) {

                description +=
                    "\n";

            }

            description +=
                `好きなもの：${pet.likes}`;

        }


        // 苦手なもの

        if (pet.dislikes) {

            if (description) {

                description +=
                    "\n";

            }

            description +=
                `苦手なもの：${pet.dislikes}`;

        }


        if (description) {

            petDescription.textContent =
                description;

        } else {

            petDescription.textContent =
                "ペット情報は登録されていません。";

        }

    }



    // ========================================
    // シェア条件
    // ========================================

    const conditionBox =
        document.querySelector(
            ".condition-box"
        );


    if (conditionBox) {

        if (pet.shareConditions) {

            conditionBox.innerHTML = `
                <p>
                    ✓ ${escapeHtml(
                        pet.shareConditions
                    )}
                </p>
            `;

        } else {

            conditionBox.innerHTML = `
                <p>
                    シェア条件は登録されていません。
                </p>
            `;

        }

    }



    // ========================================
    // デバッグ用
    // ========================================

    console.log(
        "表示したペットID:",
        pet.petId
    );

}


// ========================================
// 日付を「○月○日」に変換
// ========================================

function formatDate(dateString) {

    if (
        !dateString ||
        dateString === "null" ||
        dateString === "undefined"
    ) {

        return "";

    }


    const date =
        new Date(dateString);


    if (
        Number.isNaN(
            date.getTime()
        )
    ) {

        return "";

    }


    return `${date.getMonth() + 1}月${date.getDate()}日`;

}


// ========================================
// HTMLエスケープ
// ========================================

function escapeHtml(value) {

    if (value === null ||
        value === undefined) {

        return "";

    }


    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");

}


// ========================================
// 初期処理
// ========================================

loadPetDetail();