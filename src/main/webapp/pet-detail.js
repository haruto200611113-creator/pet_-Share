// ========================================
// プロフィール編集ページ
// ========================================

// ========================================
// 要素取得
// ========================================

const backButton =
document.getElementById("backButton");

const profileImageInput =
document.getElementById("profileImageInput");

const profileImagePreview =
document.getElementById("profileImagePreview");

const profileImageDefault =
document.getElementById("profileImageDefault");

const accountName =
document.getElementById("accountName");

const selfIntroduction =
document.getElementById("selfIntroduction");

const saveProfileButton =
document.getElementById("saveProfileButton");

const cancelButton =
document.getElementById("cancelButton");

// ========================================
// プロフィールデータ
// ========================================

const defaultProfileData = {

accountName: "ユーザー名",

selfIntroduction: "",

profileImage: ""

};

function getProfileData() {


const savedData =
    localStorage.getItem("profileData");


if (!savedData) {

    return defaultProfileData;

}


try {

    return {

        ...defaultProfileData,

        ...JSON.parse(savedData)

    };

} catch (error) {

    console.error(
        "プロフィールデータの読み込みに失敗しました。",
        error
    );

    return defaultProfileData;

}


}

let profileData =
getProfileData();

// ========================================
// プロフィール表示
// ========================================

function displayProfileData() {


if (accountName) {

    accountName.value =
        profileData.accountName || "";

}


if (selfIntroduction) {

    selfIntroduction.value =
        profileData.selfIntroduction || "";

}


displayProfileImage();


}

// ========================================
// プロフィール画像表示
// ========================================

function displayProfileImage() {


if (
    profileData.profileImage &&
    profileImagePreview
) {

    profileImagePreview.src =
        profileData.profileImage;

    profileImagePreview.style.display =
        "block";


    if (profileImageDefault) {

        profileImageDefault.style.display =
            "none";

    }

} else {

    if (profileImagePreview) {

        profileImagePreview.src = "";

        profileImagePreview.style.display =
            "none";

    }


    if (profileImageDefault) {

        profileImageDefault.style.display =
            "block";

    }

}


}

// ========================================
// プロフィール画像変更
// ========================================

if (profileImageInput) {


profileImageInput.addEventListener(
    "change",
    function () {

        const file =
            this.files[0];


        if (!file) {

            return;

        }


        if (!file.type.startsWith("image/")) {

            alert(
                "画像ファイルを選択してください。"
            );

            this.value = "";

            return;

        }


        // 5MBまで

        const maxSize =
            5 * 1024 * 1024;


        if (file.size > maxSize) {

            alert(
                "画像サイズは5MB以下にしてください。"
            );

            this.value = "";

            return;

        }


        const reader =
            new FileReader();


        reader.onload =
            function (event) {

                profileData.profileImage =
                    event.target.result;

                displayProfileImage();

            };


        reader.onerror =
            function () {

                alert(
                    "画像の読み込みに失敗しました。"
                );

            };


        reader.readAsDataURL(file);

    }
);

}

// ========================================
// プロフィール保存
// ========================================

if (saveProfileButton) {

saveProfileButton.addEventListener(
    "click",
    function () {


        const newAccountName =
            accountName.value.trim();


        const newSelfIntroduction =
            selfIntroduction.value.trim();


        if (!newAccountName) {

            alert(
                "アカウント名を入力してください。"
            );

            accountName.focus();

            return;

        }


        profileData.accountName =
            newAccountName;


        profileData.selfIntroduction =
            newSelfIntroduction;


        localStorage.setItem(
            "profileData",
            JSON.stringify(profileData)
        );


        alert(
            "プロフィールを保存しました。"
        );

    }
);


}

// ========================================
// 戻るボタン
// ========================================

if (backButton) {

backButton.addEventListener(
    "click",
    function () {

        // ひとつ前のページへ戻る

        window.history.back();

    }
);

}

// ========================================
// キャンセル
// ========================================

if (cancelButton) {

cancelButton.addEventListener(
    "click",
    function () {


        const result =
            confirm(
                "変更内容を破棄して前のページに戻りますか？"
            );


        if (!result) {

            return;

        }


        // ひとつ前のページへ戻る

        window.history.back();

    }
);


}

// ========================================
// 初期表示
// ========================================

displayProfileData();
