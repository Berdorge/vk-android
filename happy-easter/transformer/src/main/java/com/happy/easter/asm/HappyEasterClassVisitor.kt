package com.happy.easter.asm

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

private const val ASM_API_VERSION = Opcodes.ASM7

class HappyEasterClassVisitor(
    classVisitor: ClassVisitor
) : ClassVisitor(ASM_API_VERSION, classVisitor) {
    override fun visitMethod(
        access: Int,
        methodName: String,
        methodDesc: String,
        signature: String?,
        exceptions: Array<String>?
    ): MethodVisitor {
        val methodVisitor = super.visitMethod(access, methodName, methodDesc, signature, exceptions)
        return HappyEasterMethodVisitor(
            ASM_API_VERSION,
            methodVisitor,
            access,
            methodName,
            methodDesc
        )
    }
}
