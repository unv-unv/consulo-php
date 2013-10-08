package org.consulo.php.lang.psi.impl.stub.elements;

import java.io.IOException;

import org.consulo.php.lang.PhpLanguage;
import org.consulo.php.lang.psi.PhpFile;
import org.consulo.php.lang.psi.impl.stub.PhpFileStub;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.DefaultStubBuilder;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.tree.IStubFileElementType;

/**
 * @author VISTALL
 * @since 16.07.13.
 */
public class PhpFileStubElement extends IStubFileElementType<PhpFileStub>
{
	public PhpFileStubElement()
	{
		super("PHP_FILE", PhpLanguage.INSTANCE);
	}

	@Override
	public StubBuilder getBuilder()
	{
		return new DefaultStubBuilder()
		{
			@Override
			protected StubElement createStubForFile(@NotNull PsiFile file)
			{
				if(file instanceof PhpFile)
				{
					return new PhpFileStub((PhpFile) file);
				}

				return super.createStubForFile(file);
			}
		};
	}

	@NotNull
	@Override
	public PhpFileStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException
	{
		return new PhpFileStub(null);
	}

	@Override
	public int getStubVersion()
	{
		return 2;
	}

	@NotNull
	@Override
	public String getExternalId()
	{
		return "php.file";
	}

	@Override
	public boolean shouldBuildStubFor(VirtualFile file)
	{
		return false;
	}
}
