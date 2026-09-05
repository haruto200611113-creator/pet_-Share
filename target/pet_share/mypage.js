// ========================================
// マイページ
// ========================================


// ========================================
// 初期ユーザーデータ
// ========================================

const defaultUserData = {

    userId: 1,

    accountName: "ユーザー名",

    name: "山田 太郎",

    email: "example@example.com",

    birthDate: "2006-01-01",

    gender: "男性",

    phone: "090-1234-5678",

    postalCode: "750-0000",

    prefecture: "山口県",

    city: "下関市",

    address: "未登録",

    building: "",

    profileImage: "",

    postCount: 0,

    likeCount: 0,

    shareCount: 0,

    applicationCount: 0

};



// ========================================
// ユーザーデータ取得
// ========================================

function getUserData() {

    const savedData =
        localStorage.getItem(
            "profileData"
        );


    // 保存データがない場合

    if (!savedData) {

        return defaultUserData;

    }


    try {

        return {

            ...defaultUserData,

            ...JSON.parse(savedData)

        };

    } catch (error) {

        console.error(
            "ユーザーデータの読み込みに失敗しました。",
            error
        );

        return defaultUserData;
    }
}



// ========================================
// マイページ表示
// ========================================

function displayUserData() {

    const userData =
        getUserData();



    // ====================================
    // アカウント名
    // ====================================

    const userName =
        document.getElementById(
            "userName"
        );


    if (userName) {

        userName.textContent =
            userData.accountName ||
            "ユーザー名";

    }



    // ====================================
    // 活動エリア
    // ====================================

    const userArea =
        document.getElementById(
            "userArea"
        );


    if (userArea) {

        const prefecture =
            userData.prefecture ||
            "未登録";


        userArea.textContent =
            "📍 " + prefecture;

    }



    // ====================================
    // 自分の投稿数
    // ====================================

    const postCount =
        document.getElementById(
            "postCount"
        );


    if (postCount) {

        postCount.textContent =
            userData.postCount || 0;

    }



    // ====================================
    // いいね数
    // ====================================

    const likeCount =
        document.getElementById(
            "likeCount"
        );


    if (likeCount) {

        likeCount.textContent =
            userData.likeCount || 0;

    }



    // ====================================
    // シェア履歴
    // ====================================

    const shareCount =
        document.getElementById(
            "shareCount"
        );


    if (shareCount) {

        shareCount.textContent =
            userData.shareCount || 0;

    }



    // ====================================
    // 申し込み状況
    // ====================================

    const applicationCount =
        document.getElementById(
            "applicationCount"
        );


    if (applicationCount) {

        applicationCount.textContent =
            userData.applicationCount || 0;

    }



    // ====================================
    // プロフィール画像
    // ====================================

    const profileImage =
        document.getElementById(
            "profileImage"
        );


    const profileDefaultIcon =
        document.getElementById(
            "profileDefaultIcon"
        );


    if (
        profileImage &&
        profileDefaultIcon
    ) {


        // プロフィール画像がある場合

        if (userData.profileImage) {

            profileImage.src =
                userData.profileImage;

            profileImage.style.display =
                "block";

            profileDefaultIcon.style.display =
                "none";

        }


        // プロフィール画像がない場合

        else {

            profileImage.style.display =
                "none";

            profileDefaultIcon.style.display =
                "block";

        }

    }

}



// ========================================
// ホームへ戻る
// ========================================

const backButton =
    document.getElementById(
        "backButton"
    );


if (backButton) {

    backButton.addEventListener(
        "click",
        function () {

            window.location.href =
                "home.html";

        }
    );

}



// ========================================
// 利用規約
// ========================================

const termsLink =
    document.getElementById(
        "termsLink"
    );


if (termsLink) {

    termsLink.addEventListener(
        "click",
        function () {

            sessionStorage.setItem(
                "termsFrom",
                "mypage"
            );

        }
    );

}



// ========================================
// プライバシーポリシー
// ========================================

const privacyLink =
    document.getElementById(
        "privacyLink"
    );


if (privacyLink) {

    privacyLink.addEventListener(
        "click",
        function () {

            sessionStorage.setItem(
                "privacyFrom",
                "mypage"
            );

        }
    );

}



// ========================================
// 初期表示
// ========================================

displayUserData();