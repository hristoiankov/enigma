# Enigma Text Editor
<p align="center">
  <img alt="Enigma" src="https://raw.githubusercontent.com/hristoiankov/enigma/master/md-res/enigma-short.png"
</p>

Enigma began as an idea to provide a simple standalone multi-platform solution for storing sensitive data. In its current state Enigma is a text editor with file encryption/decryption capability. The application utilizes the trusted Java Cryptography Extension (JCE) in the back-end for its encryption capability. Enigma further employs steganography techniques to conceal data into other inconspicuous file types.

<p align="center">
  <img alt="Enigma" src="https://raw.githubusercontent.com/hristoiankov/enigma/master/md-res/sc-01.png">
</p>

Since data that has been encrypted appears as uniformly random noise, this data embedded within an image or another host media becomes almost undetectable and the host remains unsuspecting of containing anything other than its main content. The purpose of this application is to allow anyone to secure personal data and hide it in plain sight.

<p align="center">
  <img alt="Enigma" src="https://raw.githubusercontent.com/hristoiankov/enigma/master/md-res/sc-03.png">
</p>

An advanced feature of Enigma is binary viewer which opens any file as an 8 bit grayscale image where each pixel represents each byte of the data. The image becomes a visual method of viewing raw data of any file. One of the below images will embed this encrypted data inside of itself.

<p align="center">
  <img alt="Enigma" src="https://raw.githubusercontent.com/hristoiankov/enigma/master/md-res/potw1034a-s1.png">
  <img alt="Enigma" src="https://raw.githubusercontent.com/hristoiankov/enigma/master/md-res/potw1034a-s1-out.png">
</p>

The two images above are visually the same. The image on the right uses the left image as a host but it also contains encrypted data.

<p align="center">
  <img alt="Enigma" src="https://raw.githubusercontent.com/hristoiankov/enigma/master/md-res/enigma-sc-4.png">
</p>

When the encrypted image is opened and the correct key is supplied, these are the contents revealed. The password for the image is **password** for anyone that wants to try this themselves.

***Warning**: Be careful when encrypting data. If the key is lost or forgotten, the data will be lost forever.*

---

**Detailed Specifications:**
* Hashing Algorithm: SHA-1 + random salt
* Cipher Algorithm: AES
* Encryption Algorithm: AES128 + random initialialization vector (iv)
* Steganography Algorithm: Least Significant Bit (LSB)

**System Requirements:**
* Operating System: Windows, Linux, Unix
* Minimum Java Version: Java 8

**Download:**

https://github.com/hristoiankov/enigma/releases/download/0.14/enigma-0.14.jar
