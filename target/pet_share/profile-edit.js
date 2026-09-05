document.addEventListener("DOMContentLoaded", function () {

    console.log("profile-edit.js 読み込み成功");

    // =========================
    // 要素取得
    // =========================

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

    const backButton =
        document.getElementById("backButton");


    // =========================
    // 要素チェック
    // =========================

    console.log("画像入力:", profileImageInput);
    console.log("画像プレビュー:", profileImagePreview);
    console.log("デフォルト画像:", profileImageDefault);


    // =========================
    // プロフィール画像
    // =========================

    profileImageInput.addEventListener("change", function () {

        console.log("画像が選択されました");

        const file = this.files[0];

        if (!file) {
            console.log("ファイルがありません");
            return;
        }

        console.log("選択されたファイル:", file);


        // 画像かチェック
        if (!file.type.startsWith("image/")) {

            alert("画像ファイルを選択してください。");

            this.value = "";

            return;
        }


        // 選択した画像を表示
        const imageUrl = URL.createObjectURL(file);

        profileImagePreview.src = imageUrl;

        profileImagePreview.style.display = "block";

        profileImageDefault.style.display = "none";


        console.log("画像表示完了");
    });


    // =========================
    // 保存
    // =========================

    saveProfileButton.addEventListener("click", function () {

        const name =
            accountName.value.trim();

        const introduction =
            selfIntroduction.value.trim();


        if (name === "") {

            alert("アカウント名を入力してください。");

            accountName.focus();

            return;
        }


        const profileData = {

            accountName: name,

            selfIntroduction: introduction,

            profileImage:
                profileImagePreview.src || ""
        };


        localStorage.setItem(
            "profileData",
            JSON.stringify(profileData)
        );


        alert("プロフィールを更新しました。");

        window.location.href = "mypage.html";
    });


    // =========================
    // 戻る
    // =========================

    backButton.addEventListener("click", function () {

        window.location.href = "mypage.html";

    });


    // =========================
    // キャンセル
    // =========================

    cancelButton.addEventListener("click", function () {

        const result = confirm(
            "変更内容を破棄してマイページに戻りますか？"
        );

        if (result) {

            window.location.href = "mypage.html";

        }

    });

});