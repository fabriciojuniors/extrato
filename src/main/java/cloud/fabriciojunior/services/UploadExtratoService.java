package cloud.fabriciojunior.services;

import cloud.fabriciojunior.config.RegraNegocioException;
import cloud.fabriciojunior.entities.Extrato;
import cloud.fabriciojunior.repositories.ExtratoRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@RequestScoped
public class UploadExtratoService {

    final String EXTENSAO_OFX = ".ofx";

    @Inject
    Logger logger;

    @Inject
    ExtratoRepository extratoRepository;

    public void upload(final MultipartFormDataInput input) {
        var isUploadValid = input.getParts().stream()
                        .allMatch(part -> part.getFileName().toLowerCase().endsWith(EXTENSAO_OFX));

        if (!isUploadValid) {
            throw new RegraNegocioException("Os arquivos devem possuir a extensão OFX");
        }

        input.getParts().stream()
                .filter(part -> part.getFileName().toLowerCase().endsWith(EXTENSAO_OFX))
                .forEach(this::upload);
    }

    private void upload(final InputPart part) {
        final String partName = part.getFileName();
        try {
            final InputStream inputStream = part.getBody(InputStream.class, null);
            final byte[] bytes = IOUtils.toByteArray(inputStream);

            final String path = FileUtils.getTempDirectoryPath();

            if (path == null) {
                throw new IllegalArgumentException("A pasta 'resource' não existe");
            }

            final File customDir = new File(path);
            final String fileName = customDir.getAbsolutePath() + File.separator + partName;

            Files.write(Paths.get(fileName), bytes, StandardOpenOption.CREATE_NEW);
            createExtrato(fileName);
        } catch (FileAlreadyExistsException e) {
            throw new RegraNegocioException(String.format("O arquivo informado já foi processado. (Arquivo: %s)", partName));
        } catch (IOException e) {
            throw new RegraNegocioException(String.format("Erro ao realizar upload do arquivo. Motivo: %s", e.getMessage()));
        }
    }

    private void createExtrato(final String fileName) {
        final Extrato extrato = new Extrato();
        extrato.setNomeArquivo(fileName);
        extratoRepository.persist(extrato);
    }

}
