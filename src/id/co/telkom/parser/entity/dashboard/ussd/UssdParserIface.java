package id.co.telkom.parser.entity.dashboard.ussd;

import java.io.File;

import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;

public interface UssdParserIface{
	void ProcessFile(File file,  final LoaderHandlerManager loader,  final Context ctx, UssdParser10 parser)  throws Exception;
}
