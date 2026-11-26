package com.petshare;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.Base64;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

/**
 * ペット登録Servlet
 *
 * URL:
 * /pet-register
 *
 * 処理内容:
 * 1. 本会員か確認
 * 2. 入力されたペット情報を取得
 * 3. ペット画像を受け取る
 * 4. JPEG ImageReaderを直接使用して画像を読み込む
 * 5. サーバー側で画像をリサイズ
 * 6. JPEGへ変換
 * 7. Base64形式に変換
 * 8. PetDAOでDBへ登録
 */
@WebServlet("/pet-register")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, // 1ファイル最大5MB
                maxRequestSize = 10 * 1024 * 1024, // リクエスト全体最大10MB
                fileSizeThreshold = 1024 * 1024 // 1MBを超えたら一時ファイル使用
)
public class PetRegisterServlet extends HttpServlet {

        private static final long serialVersionUID = 1L;

        // ==========================================
        // 画像設定
        // ==========================================

        // 保存する画像の最大サイズ
        private static final int MAX_IMAGE_WIDTH = 1280;
        private static final int MAX_IMAGE_HEIGHT = 1280;

        // JPEG品質
        private static final float JPEG_QUALITY = 0.80f;

        @Override
        protected void doPost(
                        HttpServletRequest request,
                        HttpServletResponse response)
                        throws ServletException, IOException {

                System.out.println("===== PetRegisterServlet doPost 開始 =====");

                request.setCharacterEncoding("UTF-8");
                response.setContentType("text/html; charset=UTF-8");

                // ==========================================
                // ログイン・本会員チェック
                // ==========================================

                HttpSession session = request.getSession(false);

                if (!AccessControl.requireFullMember(session, response)) {
                        return;
                }

                // ==========================================
                // accountId取得
                // ==========================================

                Object accountIdObject = session.getAttribute("accountId");

                if (accountIdObject == null) {

                        System.out.println(
                                        "accountIdがセッションにありません。");

                        response.sendRedirect(
                                        request.getContextPath()
                                                        + "/login.html");

                        return;
                }

                int accountId;

                try {

                        accountId = Integer.parseInt(
                                        accountIdObject.toString());

                } catch (NumberFormatException e) {

                        System.err.println(
                                        "accountIdの取得に失敗しました。");

                        response.sendRedirect(
                                        request.getContextPath()
                                                        + "/login.html");

                        return;
                }

                // ==========================================
                // Petオブジェクト作成
                // ==========================================

                Pet pet = new Pet();

                pet.setAccountId(accountId);

                // ==========================================
                // 基本情報
                // ==========================================

                pet.setPetName(
                                getParameter(
                                                request,
                                                "petName"));

                pet.setPetType(
                                getParameter(
                                                request,
                                                "petType"));

                pet.setBreed(
                                getParameter(
                                                request,
                                                "breed"));

                pet.setGender(
                                getParameter(
                                                request,
                                                "gender"));

                pet.setBirthDate(
                                parseDate(
                                                request.getParameter(
                                                                "birthDate")));

                pet.setWeight(
                                parseDouble(
                                                request.getParameter(
                                                                "weight")));

                // ==========================================
                // 性格・特徴
                // ==========================================

                pet.setPersonality(
                                getParameter(
                                                request,
                                                "personality"));

                pet.setFeatures(
                                getParameter(
                                                request,
                                                "features"));

                pet.setLikes(
                                getParameter(
                                                request,
                                                "likes"));

                pet.setDislikes(
                                getParameter(
                                                request,
                                                "dislikes"));

                // ==========================================
                // 相性
                // ==========================================

                pet.setOtherAnimals(
                                getParameter(
                                                request,
                                                "otherAnimals"));

                pet.setChildrenCompatibility(
                                getParameter(
                                                request,
                                                "childrenCompatibility"));

                // ==========================================
                // 飼育情報
                // ==========================================

                pet.setFoodInfo(
                                getParameter(
                                                request,
                                                "foodInfo"));

                pet.setWalkInfo(
                                getParameter(
                                                request,
                                                "walkInfo"));

                pet.setToiletInfo(
                                getParameter(
                                                request,
                                                "toiletInfo"));

                pet.setAloneTime(
                                getParameter(
                                                request,
                                                "aloneTime"));

                pet.setCareInfo(
                                getParameter(
                                                request,
                                                "careInfo"));

                // ==========================================
                // 健康・安全情報
                // ==========================================

                pet.setVaccination(
                                getParameter(
                                                request,
                                                "vaccination"));

                pet.setNeutering(
                                getParameter(
                                                request,
                                                "neutering"));

                pet.setMedicalHistory(
                                getParameter(
                                                request,
                                                "medicalHistory"));

                pet.setAllergies(
                                getParameter(
                                                request,
                                                "allergies"));

                pet.setMedication(
                                getParameter(
                                                request,
                                                "medication"));

                pet.setPrecautions(
                                getParameter(
                                                request,
                                                "precautions"));

                // ==========================================
                // シェア情報
                // ==========================================

                pet.setSharePrice(
                                parseInteger(
                                                request.getParameter(
                                                                "sharePrice")));

                pet.setShareStartDate(
                                parseDate(
                                                request.getParameter(
                                                                "shareStartDate")));

                pet.setShareEndDate(
                                parseDate(
                                                request.getParameter(
                                                                "shareEndDate")));

                pet.setShareConditions(
                                getParameter(
                                                request,
                                                "shareConditions"));

                // ==========================================
                // ペット画像処理
                // ==========================================

                try {

                        Part imagePart = request.getPart("petImage");

                        // --------------------------------------
                        // 画像が選択されている場合
                        // --------------------------------------

                        if (imagePart != null
                                        && imagePart.getSize() > 0) {

                                String contentType = imagePart.getContentType();

                                String fileName = imagePart.getSubmittedFileName();

                                long fileSize = imagePart.getSize();

                                System.out.println(
                                                "画像Content-Type = "
                                                                + contentType);

                                System.out.println(
                                                "画像ファイル名 = "
                                                                + fileName);

                                System.out.println(
                                                "画像ファイルサイズ = "
                                                                + fileSize
                                                                + " bytes");

                                // ----------------------------------
                                // 画像ファイルか確認
                                // ----------------------------------

                                if (contentType == null
                                                || !contentType.startsWith("image/")) {

                                        System.out.println(
                                                        "画像ファイルではありません。");

                                        response.sendRedirect(
                                                        request.getContextPath()
                                                                        + "/pet-register.html"
                                                                        + "?error=image");

                                        return;
                                }

                                // ----------------------------------
                                // ファイルサイズチェック
                                // ----------------------------------

                                if (fileSize > 5 * 1024 * 1024) {

                                        System.out.println(
                                                        "画像ファイルサイズが5MBを超えています。");

                                        response.sendRedirect(
                                                        request.getContextPath()
                                                                        + "/pet-register.html"
                                                                        + "?error=image_size");

                                        return;
                                }

                                // ==================================
                                // ImageIOプラグインスキャン
                                // ==================================

                                ImageIO.scanForPlugins();

                                System.out.println(
                                                "ImageIOプラグインのスキャン完了");

                                // ==================================
                                // JPEG ImageReader確認
                                // ==================================

                                System.out.println(
                                                "===== JPEG ImageReader確認 =====");

                                Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(
                                                "JPEG");

                                int readerCount = 0;

                                while (readers.hasNext()) {

                                        ImageReader reader = readers.next();

                                        readerCount++;

                                        System.out.println(
                                                        "JPEG Reader = "
                                                                        + reader.getClass()
                                                                                        .getName());
                                }

                                System.out.println(
                                                "JPEG Reader数 = "
                                                                + readerCount);

                                System.out.println(
                                                "================================");

                                if (readerCount == 0) {

                                        System.err.println(
                                                        "JPEG ImageReaderが見つかりません。");

                                        response.sendRedirect(
                                                        request.getContextPath()
                                                                        + "/pet-register.html"
                                                                        + "?error=image");

                                        return;
                                }

                                // ==================================
                                // 画像byte[]読み込み
                                // ==================================

                                byte[] imageBytes;

                                try (InputStream inputStream = imagePart.getInputStream()) {

                                        imageBytes = inputStream.readAllBytes();
                                }

                                System.out.println(
                                                "画像byte[]読み込み成功 = "
                                                                + imageBytes.length
                                                                + " bytes");

                                // ==================================
                                // JPEG Readerを直接使用
                                // ==================================

                                BufferedImage originalImage = null;

                                ImageReader jpegReader = null;

                                ImageInputStream imageInputStream = null;

                                try {

                                        System.out.println(
                                                        "===== JPEG画像を直接読み込み =====");

                                        // --------------------------------
                                        // JPEG Reader取得
                                        // --------------------------------

                                        Iterator<ImageReader> jpegReaders = ImageIO
                                                        .getImageReadersByFormatName(
                                                                        "JPEG");

                                        if (!jpegReaders.hasNext()) {

                                                throw new IOException(
                                                                "JPEG ImageReaderが見つかりません。");
                                        }

                                        /*
                                         * 今回の環境では
                                         *
                                         * com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader
                                         *
                                         * と
                                         *
                                         * com.sun.imageio.plugins.jpeg.JPEGImageReader
                                         *
                                         * の2種類が確認されています。
                                         *
                                         * 先に登録されているReaderを使用します。
                                         */

                                        jpegReader = jpegReaders.next();

                                        System.out.println(
                                                        "使用するJPEG Reader = "
                                                                        + jpegReader
                                                                                        .getClass()
                                                                                        .getName());

                                        // --------------------------------
                                        // byte[]からImageInputStream作成
                                        // --------------------------------

                                        imageInputStream = ImageIO.createImageInputStream(
                                                        new ByteArrayInputStream(
                                                                        imageBytes));

                                        if (imageInputStream == null) {

                                                throw new IOException(
                                                                "ImageInputStreamの作成に失敗しました。");
                                        }

                                        // --------------------------------
                                        // Readerへ入力をセット
                                        // --------------------------------

                                        jpegReader.setInput(
                                                        imageInputStream,
                                                        true,
                                                        true);

                                        // --------------------------------
                                        // JPEG画像を直接読み込む
                                        // --------------------------------

                                        originalImage = jpegReader.read(0);

                                        if (originalImage == null) {

                                                throw new IOException(
                                                                "JPEG ImageReaderが画像を読み込めませんでした。");
                                        }

                                        System.out.println(
                                                        "JPEG画像の読み込み成功");

                                        System.out.println(
                                                        "元画像サイズ = "
                                                                        + originalImage.getWidth()
                                                                        + " × "
                                                                        + originalImage.getHeight());

                                } finally {

                                        if (jpegReader != null) {
                                                jpegReader.dispose();
                                        }

                                        if (imageInputStream != null) {
                                                imageInputStream.close();
                                        }
                                }

                                // ==================================
                                // リサイズ
                                // ==================================

                                BufferedImage resizedImage = resizeImage(originalImage);

                                // ==================================
                                // JPEGへ変換
                                // ==================================

                                byte[] jpegBytes = convertToJpeg(
                                                resizedImage);

                                // ==================================
                                // Base64へ変換
                                // ==================================

                                String base64 = Base64.getEncoder()
                                                .encodeToString(
                                                                jpegBytes);

                                String imageData = "data:image/jpeg;base64,"
                                                + base64;

                                pet.setPetImage(
                                                imageData);

                                System.out.println(
                                                "ペット画像処理成功");

                                System.out.println(
                                                "元画像サイズ: "
                                                                + originalImage.getWidth()
                                                                + " × "
                                                                + originalImage.getHeight());

                                System.out.println(
                                                "変換後サイズ: "
                                                                + resizedImage.getWidth()
                                                                + " × "
                                                                + resizedImage.getHeight());

                                System.out.println(
                                                "変換後ファイルサイズ: "
                                                                + jpegBytes.length
                                                                + " bytes");

                                System.out.println(
                                                "Base64画像サイズ: "
                                                                + imageData.length()
                                                                + " characters");

                        } else {

                                // ==================================
                                // 画像なし
                                // ==================================

                                pet.setPetImage(null);

                                System.out.println(
                                                "ペット画像は設定されていません。");

                        }

                } catch (Exception e) {

                        // ==========================================
                        // 画像処理エラー
                        // ==========================================

                        System.err.println(
                                        "======================================");

                        System.err.println(
                                        "ペット画像の処理中にエラーが発生しました。");

                        System.err.println(
                                        "エラー内容 = "
                                                        + e.getMessage());

                        e.printStackTrace();

                        System.err.println(
                                        "======================================");

                        response.sendRedirect(
                                        request.getContextPath()
                                                        + "/pet-register.html"
                                                        + "?error=image");

                        return;
                }

                // ==========================================
                // 登録前ログ
                // ==========================================

                System.out.println(
                                "=================================");

                System.out.println(
                                "===== ペット登録開始 =====");

                System.out.println(
                                "accountId = "
                                                + accountId);

                System.out.println(
                                "petName = "
                                                + pet.getPetName());

                System.out.println(
                                "petType = "
                                                + pet.getPetType());

                System.out.println(
                                "weight = "
                                                + pet.getWeight());

                System.out.println(
                                "sharePrice = "
                                                + pet.getSharePrice());

                System.out.println(
                                "image = "
                                                + (pet.getPetImage() != null
                                                                ? "あり"
                                                                : "なし"));

                System.out.println(
                                "=================================");

                // ==========================================
                // DBへ登録
                // ==========================================

                PetDAO dao = new PetDAO();

                boolean result = dao.insertPet(pet);

                // ==========================================
                // 登録結果
                // ==========================================

                System.out.println(
                                "===== ペット登録結果 =====");

                System.out.println(
                                "result = "
                                                + result);

                // ==========================================
                // 登録成功
                // ==========================================

                if (result) {

                        System.out.println(
                                        "ペット登録成功");

                        response.sendRedirect(
                                        request.getContextPath()
                                                        + "/home.html?petRegistered=true");

                } else {

                        System.out.println(
                                        "ペット登録失敗");

                        response.sendRedirect(
                                        request.getContextPath()
                                                        + "/pet-register.html?error=true");
                }
        }

        // ==================================================
        // 文字列取得
        // ==================================================

        private String getParameter(
                        HttpServletRequest request,
                        String parameterName) {

                String value = request.getParameter(
                                parameterName);

                if (value == null) {
                        return null;
                }

                value = value.trim();

                if (value.isEmpty()) {
                        return null;
                }

                return value;
        }

        // ==================================================
        // 日付変換
        // ==================================================

        private Date parseDate(
                        String value) {

                if (value == null
                                || value.trim().isEmpty()) {

                        return null;
                }

                try {

                        return Date.valueOf(
                                        value.trim());

                } catch (IllegalArgumentException e) {

                        System.err.println(
                                        "日付変換に失敗しました: "
                                                        + value);

                        return null;
                }
        }

        // ==================================================
        // Double変換
        // ==================================================

        private Double parseDouble(
                        String value) {

                if (value == null
                                || value.trim().isEmpty()) {

                        return null;
                }

                try {

                        return Double.valueOf(
                                        value.trim());

                } catch (NumberFormatException e) {

                        System.err.println(
                                        "数値変換に失敗しました: "
                                                        + value);

                        return null;
                }
        }

        // ==================================================
        // Integer変換
        // ==================================================

        private Integer parseInteger(
                        String value) {

                if (value == null
                                || value.trim().isEmpty()) {

                        return null;
                }

                try {

                        return Integer.valueOf(
                                        value.trim());

                } catch (NumberFormatException e) {

                        System.err.println(
                                        "整数変換に失敗しました: "
                                                        + value);

                        return null;
                }
        }

        // ==================================================
        // 画像リサイズ
        // ==================================================

        private BufferedImage resizeImage(
                        BufferedImage originalImage) {

                int originalWidth = originalImage.getWidth();

                int originalHeight = originalImage.getHeight();

                // ------------------------------------------
                // すでに小さい場合
                // ------------------------------------------

                if (originalWidth <= MAX_IMAGE_WIDTH
                                && originalHeight <= MAX_IMAGE_HEIGHT) {

                        return originalImage;
                }

                // ------------------------------------------
                // 縦横比を維持して縮小
                // ------------------------------------------

                double widthRatio = (double) MAX_IMAGE_WIDTH
                                / originalWidth;

                double heightRatio = (double) MAX_IMAGE_HEIGHT
                                / originalHeight;

                double ratio = Math.min(
                                widthRatio,
                                heightRatio);

                int newWidth = (int) Math.round(
                                originalWidth
                                                * ratio);

                int newHeight = (int) Math.round(
                                originalHeight
                                                * ratio);

                BufferedImage resizedImage = new BufferedImage(
                                newWidth,
                                newHeight,
                                BufferedImage.TYPE_INT_RGB);

                Graphics2D graphics = resizedImage.createGraphics();

                try {

                        graphics.setRenderingHint(
                                        RenderingHints.KEY_INTERPOLATION,
                                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                        graphics.setRenderingHint(
                                        RenderingHints.KEY_RENDERING,
                                        RenderingHints.VALUE_RENDER_QUALITY);

                        graphics.setRenderingHint(
                                        RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);

                        graphics.drawImage(
                                        originalImage,
                                        0,
                                        0,
                                        newWidth,
                                        newHeight,
                                        null);

                } finally {

                        graphics.dispose();
                }

                return resizedImage;
        }

        // ==================================================
        // JPEG変換
        // ==================================================

        private byte[] convertToJpeg(
                        BufferedImage image)
                        throws IOException {

                ByteArrayOutputStream output = new ByteArrayOutputStream();

                /*
                 * JPEG品質を指定するため、
                 * ImageIOのJPEG Writerを取得する。
                 */

                Iterator<javax.imageio.ImageWriter> writers = ImageIO.getImageWritersByFormatName(
                                "JPEG");

                if (!writers.hasNext()) {

                        throw new IOException(
                                        "JPEG ImageWriterが見つかりません。");
                }

                javax.imageio.ImageWriter writer = writers.next();

                javax.imageio.stream.ImageOutputStream imageOutput = null;

                try {

                        imageOutput = ImageIO.createImageOutputStream(
                                        output);

                        if (imageOutput == null) {

                                throw new IOException(
                                                "JPEG ImageOutputStreamの作成に失敗しました。");
                        }

                        writer.setOutput(
                                        imageOutput);

                        javax.imageio.ImageWriteParam writeParam = writer.getDefaultWriteParam();

                        if (writeParam.canWriteCompressed()) {

                                writeParam.setCompressionMode(
                                                javax.imageio.ImageWriteParam.MODE_EXPLICIT);

                                writeParam.setCompressionQuality(
                                                JPEG_QUALITY);
                        }

                        writer.write(
                                        null,
                                        new javax.imageio.IIOImage(
                                                        image,
                                                        null,
                                                        null),
                                        writeParam);

                        imageOutput.flush();

                } finally {

                        if (writer != null) {
                                writer.dispose();
                        }

                        if (imageOutput != null) {
                                imageOutput.close();
                        }
                }

                return output.toByteArray();
        }
}
