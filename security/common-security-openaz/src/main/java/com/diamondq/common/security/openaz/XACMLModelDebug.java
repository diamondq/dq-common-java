package com.diamondq.common.security.openaz;

import com.diamondq.common.security.xacml.model.ConditionalCode;
import com.diamondq.common.security.xacml.model.IConditional;
import com.diamondq.common.security.xacml.model.IFunctionArgument;
import com.diamondq.common.security.xacml.model.ILiteralBag;
import com.diamondq.common.security.xacml.model.ILiteralSingle;
import com.diamondq.common.security.xacml.model.IOperation;
import com.diamondq.common.security.xacml.model.IResource;
import com.diamondq.common.security.xacml.model.OperationCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XACMLModelDebug {

	private static final Logger sLogger = LoggerFactory.getLogger(XACMLModelDebug.class);

	public static void debug(IFunctionArgument pResult) {

		StringBuilder sb = new StringBuilder();
		debugFunctionArgument(sb, 0, pResult);
		sLogger.debug(sb.toString());
	}

	private static void debugFunctionArgument(StringBuilder pBuilder, int pDepth, IFunctionArgument pResult) {
		if (pResult instanceof IConditional)
			debugConditional(pBuilder, pDepth, (IConditional) pResult);
		else if (pResult instanceof ILiteralBag)
			debugLiteralBag(pBuilder, pDepth, (ILiteralBag) pResult);
		else if (pResult instanceof ILiteralSingle)
			debugLiteralSingle(pBuilder, pDepth, (ILiteralSingle) pResult);
		else if (pResult instanceof IOperation)
			debugOperation(pBuilder, pDepth, (IOperation) pResult);
		else if (pResult instanceof IResource)
			debugResource(pBuilder, pDepth, (IResource) pResult);
		else
			throw new UnsupportedOperationException();
	}

	private static void addPadding(StringBuilder pBuilder, int pDepth) {
		if (pDepth > 0)
			for (int i = 0; i < pDepth; i++)
				pBuilder.append(' ');
	}

	private static void debugConditional(StringBuilder pBuilder, int pDepth, IConditional pResult) {
		ConditionalCode code = pResult.getCode();
		addPadding(pBuilder, pDepth);
		pBuilder.append(code.toString());
		pBuilder.append("(\n");
		boolean first = true;
		for (IFunctionArgument arg : pResult.getOperations()) {
			if (first == true)
				first = false;
			else
				pBuilder.append(",\n");
			debugFunctionArgument(pBuilder, pDepth + 1, arg);
		}
		pBuilder.append(')');
	}

	private static void debugLiteralBag(StringBuilder pBuilder, int pDepth, ILiteralBag pResult) {
		addPadding(pBuilder, pDepth);
		pBuilder.append("LiteralBag(");
		boolean first = true;
		for (String arg : pResult.getValue()) {
			if (first == true)
				first = false;
			else
				pBuilder.append(",");
			pBuilder.append('\"');
			pBuilder.append(arg);
			pBuilder.append('\"');
		}
		pBuilder.append(')');
	}

	private static void debugLiteralSingle(StringBuilder pBuilder, int pDepth, ILiteralSingle pResult) {
		addPadding(pBuilder, pDepth);
		pBuilder.append('\"');
		pBuilder.append(pResult.getValue());
		pBuilder.append('\"');
	}

	private static void debugOperation(StringBuilder pBuilder, int pDepth, IOperation pResult) {
		addPadding(pBuilder, pDepth);
		OperationCode code = pResult.getOperation();
		pBuilder.append(code.toString());
		pBuilder.append("(\n");
		boolean first = true;
		for (IFunctionArgument arg : pResult.getArguments()) {
			if (first == true)
				first = false;
			else
				pBuilder.append(",\n");
			debugFunctionArgument(pBuilder, pDepth + 1, arg);
		}
		pBuilder.append(')');
	}

	private static void debugResource(StringBuilder pBuilder, int pDepth, IResource pResult) {
		addPadding(pBuilder, pDepth);
		pBuilder.append("Resource(");
		pBuilder.append(pResult.getResourceId());
		pBuilder.append(')');
	}

}
