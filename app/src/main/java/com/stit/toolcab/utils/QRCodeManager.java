package com.stit.toolcab.utils;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.apache.log4j.Logger;

import java.util.Hashtable;

/**
 * Created by Administrator on 2020-11-16.
 */

public class QRCodeManager {
    private static Logger logger = Logger.getLogger(QRCodeManager.class);
    /**
     * 生成简单二维码
     *
     * @param content                字符串内容
     * @param width                  二维码宽度
     * @param height                 二维码高度
     * @param character_set          编码方式（一般使用UTF-8）
     * @param error_correction_level 容错率 L：7% M：15% Q：25% H：35%
     * @param margin                 空白边距（二维码与边框的空白区域）
     * @param color_black            黑色色块
     * @param color_white            白色色块
     * @return BitMap
     */
    public static Bitmap createQRCodeBitmap(String content, int width, int height,
                                            String character_set, String error_correction_level,
                                            String margin, int color_black, int color_white) {
        /** 1.参数合法性判断 */
        if (TextUtils.isEmpty( content )) { // 字符串内容判空
            return null;
        }

        if (width < 0 || height < 0) { // 宽和高都需要>=0
            return null;
        }

        try {
            /** 2.设置二维码相关配置,生成BitMatrix(位矩阵)对象 */
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();

            if (!TextUtils.isEmpty( character_set )) {
                hints.put( EncodeHintType.CHARACTER_SET, character_set ); // 字符转码格式设置
            }

            if (!TextUtils.isEmpty( error_correction_level )) {
                hints.put( EncodeHintType.ERROR_CORRECTION, error_correction_level ); // 容错级别设置
            }

            if (!TextUtils.isEmpty( margin )) {
                hints.put( EncodeHintType.MARGIN, margin ); // 空白边距设置
            }
            BitMatrix bitMatrix = new QRCodeWriter().encode( content, BarcodeFormat.QR_CODE, width, height, hints );

            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值 */
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get( x, y )) {
                        pixels[y * width + x] = color_black; // 黑色色块像素设置
                    } else {
                        pixels[y * width + x] = color_white; // 白色色块像素设置
                    }
                }
            }
            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,之后返回Bitmap对象 */
            Bitmap bitmap = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888 );
            bitmap.setPixels( pixels, 0, width, 0, 0, width, height );
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;

    }
}
