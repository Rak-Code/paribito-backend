package com.ecommerce.project.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

/**
 * Utility class for building MIME-formatted email messages for AWS SES.
 * Supports both simple HTML emails and emails with PDF attachments.
 */
public class MimeMessageBuilder {

    private static final String BOUNDARY_PREFIX = "----=_Part_";
    private static final String CRLF = "\r\n";

    /**
     * Builds a simple MIME message for HTML emails without attachments.
     *
     * @param from Sender email address
     * @param fromName Sender display name
     * @param to Recipient email address
     * @param subject Email subject
     * @param htmlContent HTML content of the email
     * @return Base64-encoded MIME message ready for AWS SES
     */
    public static String buildSimpleMimeMessage(
            String from,
            String fromName,
            String to,
            String subject,
            String htmlContent) {
        
        StringBuilder mime = new StringBuilder();
        
        // Headers
        mime.append("From: ").append(fromName).append(" <").append(from).append(">").append(CRLF);
        mime.append("To: ").append(to).append(CRLF);
        mime.append("Subject: ").append(encodeSubject(subject)).append(CRLF);
        mime.append("MIME-Version: 1.0").append(CRLF);
        mime.append("Content-Type: text/html; charset=UTF-8").append(CRLF);
        mime.append("Content-Transfer-Encoding: quoted-printable").append(CRLF);
        mime.append(CRLF);
        
        // Body
        mime.append(encodeQuotedPrintable(htmlContent));
        
        // Encode to Base64 for AWS SES
        return Base64.getEncoder().encodeToString(mime.toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Builds a MIME message for HTML emails with PDF attachments.
     *
     * @param from Sender email address
     * @param fromName Sender display name
     * @param to Recipient email address
     * @param subject Email subject
     * @param htmlContent HTML content of the email
     * @param attachmentData PDF attachment data as byte array
     * @param attachmentName Filename for the attachment
     * @return Base64-encoded MIME message ready for AWS SES
     */
    public static String buildMimeMessage(
            String from,
            String fromName,
            String to,
            String subject,
            String htmlContent,
            byte[] attachmentData,
            String attachmentName) {
        
        String boundary = BOUNDARY_PREFIX + UUID.randomUUID().toString().replace("-", "");
        StringBuilder mime = new StringBuilder();
        
        // Headers
        mime.append("From: ").append(fromName).append(" <").append(from).append(">").append(CRLF);
        mime.append("To: ").append(to).append(CRLF);
        mime.append("Subject: ").append(encodeSubject(subject)).append(CRLF);
        mime.append("MIME-Version: 1.0").append(CRLF);
        mime.append("Content-Type: multipart/mixed; boundary=\"").append(boundary).append("\"").append(CRLF);
        mime.append(CRLF);
        
        // HTML content part
        mime.append("--").append(boundary).append(CRLF);
        mime.append("Content-Type: text/html; charset=UTF-8").append(CRLF);
        mime.append("Content-Transfer-Encoding: quoted-printable").append(CRLF);
        mime.append(CRLF);
        mime.append(encodeQuotedPrintable(htmlContent)).append(CRLF);
        mime.append(CRLF);
        
        // Attachment part
        if (attachmentData != null && attachmentData.length > 0) {
            mime.append("--").append(boundary).append(CRLF);
            mime.append("Content-Type: application/pdf; name=\"").append(attachmentName).append("\"").append(CRLF);
            mime.append("Content-Transfer-Encoding: base64").append(CRLF);
            mime.append("Content-Disposition: attachment; filename=\"").append(attachmentName).append("\"").append(CRLF);
            mime.append(CRLF);
            
            // Encode attachment to Base64 and split into 76-character lines
            String base64Attachment = Base64.getEncoder().encodeToString(attachmentData);
            for (int i = 0; i < base64Attachment.length(); i += 76) {
                int endIndex = Math.min(i + 76, base64Attachment.length());
                mime.append(base64Attachment.substring(i, endIndex)).append(CRLF);
            }
        }
        
        // Closing boundary
        mime.append("--").append(boundary).append("--").append(CRLF);
        
        // Encode entire MIME message to Base64 for AWS SES
        return Base64.getEncoder().encodeToString(mime.toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Encodes the subject line to handle special characters.
     * Uses RFC 2047 encoding for non-ASCII characters.
     *
     * @param subject The subject string
     * @return Encoded subject string
     */
    private static String encodeSubject(String subject) {
        if (subject == null || subject.isEmpty()) {
            return "";
        }
        
        // Check if subject contains non-ASCII characters
        boolean hasNonAscii = !StandardCharsets.US_ASCII.newEncoder().canEncode(subject);
        
        if (hasNonAscii) {
            // RFC 2047 encoding: =?charset?encoding?encoded-text?=
            String encoded = Base64.getEncoder().encodeToString(subject.getBytes(StandardCharsets.UTF_8));
            return "=?UTF-8?B?" + encoded + "?=";
        }
        
        return subject;
    }

    /**
     * Encodes content using quoted-printable encoding.
     * This handles special characters and ensures proper line length.
     *
     * @param content The content to encode
     * @return Quoted-printable encoded content
     */
    private static String encodeQuotedPrintable(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        StringBuilder encoded = new StringBuilder();
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        int lineLength = 0;
        
        for (byte b : bytes) {
            int value = b & 0xFF;
            
            // Characters that need encoding: < 33, > 126, or =
            if (value < 33 || value > 126 || value == 61) {
                // Special handling for CRLF
                if (value == 13) { // CR
                    continue; // Skip, will be handled with LF
                } else if (value == 10) { // LF
                    encoded.append(CRLF);
                    lineLength = 0;
                } else {
                    String hex = String.format("=%02X", value);
                    if (lineLength + hex.length() > 76) {
                        encoded.append("=").append(CRLF);
                        lineLength = 0;
                    }
                    encoded.append(hex);
                    lineLength += hex.length();
                }
            } else {
                // Safe character, append as-is
                if (lineLength >= 76) {
                    encoded.append("=").append(CRLF);
                    lineLength = 0;
                }
                encoded.append((char) value);
                lineLength++;
            }
        }
        
        return encoded.toString();
    }
}
