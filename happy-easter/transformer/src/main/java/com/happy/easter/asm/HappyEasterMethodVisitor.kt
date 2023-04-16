package com.happy.easter.asm

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

private const val INSTRUMENTATION_OBJECT =
    "com/happy/easter/instrumentation/HappyEasterInstrumentation"

class HappyEasterMethodVisitor(
    api: Int,
    methodVisitor: MethodVisitor,
    access: Int,
    methodName: String,
    methodDesc: String
) : AdviceAdapter(api, methodVisitor, access, methodName, methodDesc) {
    override fun visitMethodInsn(
        opcode: Int,
        owner: String,
        name: String,
        desc: String,
        itf: Boolean
    ) {
        if (owner == "okhttp3/Call" && name == "execute" && desc == "()Lokhttp3/Response;") {
            super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                INSTRUMENTATION_OBJECT,
                "execute",
                "(Lokhttp3/Call;)Lokhttp3/Response;",
                false
            )
        } else if (owner == "okhttp3/Call" && name == "enqueue" && desc == "(Lokhttp3/Callback;)V") {
            super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                INSTRUMENTATION_OBJECT,
                "enqueue",
                "(Lokhttp3/Call;Lokhttp3/Callback;)V",
                false
            )
        } else if (owner == "java/net/URL" && name == "openConnection" && desc == "()Ljava/net/URLConnection;") {
            super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                INSTRUMENTATION_OBJECT,
                "openConnection",
                "(Ljava/net/URL;)Ljava/net/URLConnection;",
                false
            )
        } else if (owner == "android/app/DownloadManager\$Request" && name == "<init>" && desc == "(Landroid/net/Uri;)V") {
            super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                INSTRUMENTATION_OBJECT,
                "downloadManagerRequest",
                "(Landroid/net/Uri;)Landroid/app/DownloadManager\$Request;",
                false
            )
        } else if (owner == "android/app/DownloadManager" && name == "enqueue" && desc == "(Landroid/app/DownloadManager\$Request;)J") {
            super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                INSTRUMENTATION_OBJECT,
                "enqueue",
                "(Landroid/app/DownloadManager;Landroid/app/DownloadManager\$Request;)J",
                false
            )
        } else {
            super.visitMethodInsn(opcode, owner, name, desc, itf)
        }
    }
}

