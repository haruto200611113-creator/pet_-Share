document.addEventListener("DOMContentLoaded", function () {

    const backButton =
        document.getElementById("backButton");

    const saveButton =
        document.getElementById("saveButton");

    const currentPassword =
        document.getElementById("currentPassword");

    const newPassword =
        document.getElementById("newPassword");

    const confirmPassword =
        document.getElementById("confirmPassword");


    // Back button
    if (backButton) {

        backButton.addEventListener(
            "click",
            function () {

                window.location.href =
                    "settings.html";

            }
        );

    }


    // Show / hide password
    const showButtons =
        document.querySelectorAll(".show-button");

    showButtons.forEach(function (button) {

        button.addEventListener(
            "click",
            function () {

                const targetId =
                    button.getAttribute("data-target");

                const target =
                    document.getElementById(targetId);

                if (!target) {
                    return;
                }

                if (target.type === "password") {

                    target.type = "text";

                    button.textContent =
                        "非表示";

                } else {

                    target.type = "password";

                    button.textContent =
                        "表示";

                }

            }
        );

    });


    // Password change
    if (saveButton) {

        saveButton.addEventListener(
            "click",
            async function () {

                console.log(
                    "Password change button clicked."
                );

                const current =
                    currentPassword.value;

                const newPass =
                    newPassword.value;

                const confirm =
                    confirmPassword.value;


                // Required check
                if (!current) {

                    alert(
                        "現在のパスワードを入力してください。"
                    );

                    return;

                }

                if (!newPass) {

                    alert(
                        "新しいパスワードを入力してください。"
                    );

                    return;

                }

                if (!confirm) {

                    alert(
                        "新しいパスワード（確認）を入力してください。"
                    );

                    return;

                }


                // Minimum length
                if (newPass.length < 8) {

                    alert(
                        "新しいパスワードは8文字以上で設定してください。"
                    );

                    return;

                }


                // Confirmation
                if (newPass !== confirm) {

                    alert(
                        "新しいパスワードが一致していません。"
                    );

                    return;

                }


                const contextPath =
                    window.location.pathname.substring(
                        0,
                        window.location.pathname.lastIndexOf("/")
                    );


                const servletUrl =
                    contextPath +
                    "/UpdatePasswordServlet";


                console.log(
                    "Sending password change request:"
                );

                console.log(
    servletUrl
);

// 保存中の表示
saveButton.disabled = true;
saveButton.textContent = "保存中...";

try {

                    const response =
                        await fetch(
                            servletUrl,
                            {
                                method: "POST",

                                headers: {
                                    "Content-Type":
                                        "application/x-www-form-urlencoded; charset=UTF-8"
                                },

                                body:
                                    new URLSearchParams({

                                        currentPassword:
                                            current,

                                        newPassword:
                                            newPass,

                                        confirmPassword:
                                            confirm

                                    })
                            }
                        );


                    console.log(
                        "Servlet response status:"
                    );

                    console.log(
                        response.status
                    );


                    if (response.ok) {

                        alert(
                            "パスワードを変更しました。"
                        );

                        window.location.href =
                            "settings.html";

                    } else {

                        const message =
                            await response.text();

                        console.error(
                            "Password change failed:"
                        );

                        console.error(
                            message
                        );

                        alert(
                            "パスワードの変更に失敗しました。\n" +
                            message
                        );

                    }

                               } catch (error) {

                    console.error(
                        "Password change request error:"
                    );

                    console.error(
                        error
                    );

                    alert(
                        "パスワードの変更中にエラーが発生しました。"
                    );

                } finally {

                    // 保存ボタンを元に戻す
                    saveButton.disabled = false;
                    saveButton.textContent = "保存する";

                }

            }
        );

    }

});