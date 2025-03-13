package com.pwc.common;

import com.pwc.common.constants.RpcConstants;

import java.io.Serializable;
import java.util.Arrays;

public class MyRpcProtocol implements Serializable {
    private static final long serialVersionUID = 4606155673034178606L;
    private short magicNumber = RpcConstants.MAGIC_NUMBER;
    private int contentLength;
    private byte[] content;

    public MyRpcProtocol(byte[] content) {
        this.contentLength = content.length;
        this.content = content;
    }

    public short getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(short magicNumber) {
        this.magicNumber = magicNumber;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "MyRpcProtocol{" +
                ", contentLength=" + contentLength +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
