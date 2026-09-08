const form = document.getElementById("registerForm");
const agreement = document.getElementById("agreement");
const termsStatus = document.getElementById("termsStatus");
const passwordConfirmInput = document.getElementById("confirmPassword");
const registerButton = document.getElementById("registerButton");

function saveFormData() {
    const formData = {
        accountName: document.getElementById("accountName").value,
        email: document.getElementById("email").value,
        password: document.getElementById("password").value,
        passwordConfirm: passwordConfirmInput.value,
        name: document.getElementById("name").value,
        birthDate: document.getElementById("birthDate").value,
        gender: document.querySelector('input[name="gender"]:checked')?.value || "",
        phone: document.getElementById("phone").value,
        postalCode: document.getElementById("postalCode").value,
        prefecture: document.getElementById("prefecture").value,
        city: document.getElementById("city").value,
        address: document.getElementById("address").value,
        building: document.getElementById("building").value
    };

    sessionStorage.setItem("registerFormData", JSON.stringify(formData));
}

function restoreFormData() {
    const savedData = sessionStorage.getItem("registerFormData");
    if (!savedData) return;

    let formData;
    try {
        formData = JSON.parse(savedData);
    } catch (error) {
        sessionStorage.removeItem("registerFormData");
        return;
    }

    Object.keys(formData).forEach(function (key) {
        const input = document.getElementById(
            key === "passwordConfirm" ? "confirmPassword" : key
        );
        if (input && key !== "gender") input.value = formData[key] || "";
    });

    if (formData.gender) {
        const gender = document.querySelector(
            `input[name="gender"][value="${formData.gender}"]`
        );
        if (gender) gender.checked = true;
    }
}

const termsConfirmed = sessionStorage.getItem("termsConfirmed");
if (termsConfirmed === "true") {
    agreement.disabled = false;
    termsStatus.textContent = "利用規約を確認済みです。";
}

document.getElementById("termsLink").addEventListener("click", function () {
    sessionStorage.setItem("termsFrom", "signup");
    saveFormData();
});

restoreFormData();

form.addEventListener("submit", function (event) {
    event.preventDefault();

    if (sessionStorage.getItem("termsConfirmed") !== "true") {
        alert("先に利用規約を確認してください。");
        return;
    }
    if (!agreement.checked) {
        alert("利用規約に同意してください。");
        return;
    }
    if (localStorage.getItem("petCareQuizPassed") !== "true") {
        alert("先に安全クイズへ進み、全問正解してください。");
        return;
    }

    const password = document.getElementById("password").value;
    if (password !== passwordConfirmInput.value) {
        alert("パスワードが一致していません。");
        return;
    }
    if (password.length < 8) {
        alert("パスワードは8文字以上で入力してください。");
        return;
    }
    if (!document.querySelector('input[name="gender"]:checked')) {
        alert("性別を選択してください。");
        return;
    }

    alert("新規登録が完了しました。");
    localStorage.setItem("petCareQuizPassed", "true");
    sessionStorage.removeItem("registerFormData");
    sessionStorage.removeItem("termsConfirmed");
    sessionStorage.removeItem("termsFrom");
    sessionStorage.removeItem("quizAnswersV2");
});
