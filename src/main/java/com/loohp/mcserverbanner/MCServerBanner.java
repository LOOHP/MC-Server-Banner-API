package com.loohp.mcserverbanner;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.utils.FileUtils;
import com.loohp.interactivechat.utils.HTTPRequestUtils;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.libs.LibraryDownloadManager;
import com.loohp.interactivechatdiscordsrvaddon.libs.LibraryLoader;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.ICacheManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.PackFormat;
import com.loohp.interactivechatdiscordsrvaddon.resources.PackFormatVersion;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceDownloadManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackSource;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackType;
import com.loohp.interactivechatdiscordsrvaddon.utils.TriConsumer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MCServerBanner implements AutoCloseable {

    public static final String VERSION_MANIFEST = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
    public static final PackFormatVersion PACK_FORMAT = ResourceRegistry.RESOURCE_PACK_VERSION;
    public static final String LANGUAGE = "en_us";

    private final String minecraftVersion;

    private ResourceManager resourceManager;
    private boolean verbose;

    public MCServerBanner() {
        this(true);
    }

    public MCServerBanner(boolean verbose) {
        this.verbose = verbose;
        this.minecraftVersion = fetchLatestMinecraftVersion();
        File defaultAssetsFolder = new File("MCServerBanner/built-in", "Default");
        if (!defaultAssetsFolder.exists()) {
            downloadAssets();
        }
        includeLibraries();
        reloadResourceManager();
    }

    @Deprecated
    public BufferedImage generateBanner(int width, int height, BufferedImage background, BufferedImage icon, String title, List<String> description, String players, int ping, String watermark, double scale) {
        return generateBanner(width, height, background, icon, parseJson(title), parseJson(description), parseJson(players), ping, parseJson(watermark), scale);
    }

    public BufferedImage generateBanner(int width, int height, BufferedImage background, BufferedImage icon, Component title, List<Component> description, Component players, int ping, Component watermark, double scale) {
        BufferedImage image = new BufferedImage((int) (width * scale), (int) (height * scale), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(background, 0, 0, image.getWidth(), image.getHeight(), null);
            image = ImageUtils.printComponentShadowless(resourceManager, image, title, LANGUAGE, false, (int) (76 * scale), (int) (7 * scale), (float) (16 * scale)).getImage();
            int i = 0;
            for (Component line : description) {
                image = ImageUtils.printComponentShadowless(resourceManager, image, line, LANGUAGE, false, (int) (76 * scale), (int) (29 * scale) + (int) (i * 18 * scale), (float) (16 * scale)).getImage();
                i++;
            }
            g.drawImage(icon, (int) (5 * scale), (int) (5 * scale), (int) (64 * scale), (int) (64 * scale), null);
            BufferedImage pingIcon = getPingIcon(ping);
            g.drawImage(pingIcon, image.getWidth() - (int) (27 * scale), (int) (7 * scale), (int) (20 * scale), (int) (15.5 * scale), null);
            image = ImageUtils.printComponentRightAlignedShadowless(resourceManager, image, players, LANGUAGE, false, image.getWidth() - (int) (34 * scale), (int) (8 * scale), (float) (16 * scale)).getImage();
            image = ImageUtils.printComponentRightAlignedShadowless(resourceManager, image, watermark, LANGUAGE, false, image.getWidth(), image.getHeight() - (int) (8 * scale), (float) (6 * scale)).getImage();
        } finally {
            g.dispose();
        }
        return image;
    }

    private BufferedImage getPingIcon(int ms) {
        String location = "minecraft:icon/";
        if (ms < 0) {
            location = location + "ping_unknown";
        } else if (ms < 150) {
            location = location + "ping_5";
        } else if (ms < 300) {
            location = location + "ping_4";
        } else if (ms < 600) {
            location = location + "ping_3";
        } else if (ms >= 1000) {
            location = location + "ping_1";
        } else {
            location = location + "ping_2";
        }
        return resourceManager.getTextureManager().getTexture(location).getTexture(true);
    }

    private List<Component> parseJson(List<String> json) {
        return json.stream().map(this::parseJson).collect(Collectors.toList());
    }

    private Component parseJson(String json) {
        return GsonComponentSerializer.gson().deserialize(json);
    }

    public String fetchLatestMinecraftVersion() {
        JSONObject json = HTTPRequestUtils.getJSONResponse(VERSION_MANIFEST);
        return (String) ((JSONObject) json.get("latest")).get("release");
    }

    public void reloadResourceManager() {
        resourceManager = new ResourceManager(PACK_FORMAT, Collections.emptyList(), Collections.singletonList(ICacheManager.getDummySupplier()), PackFormat.version(PACK_FORMAT), ResourceManager.Flag.build(false, false));
        resourceManager.loadResources(Collections.singletonList(ResourcePackSource.ofDefault("Default", new File("MCServerBanner/built-in", "Default"), ResourcePackType.BUILT_IN)), (s, i) -> {});
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void downloadAssets() {
        File defaultAssetsFolder = new File("MCServerBanner/built-in", "Default");
        if (defaultAssetsFolder.exists()) {
            FileUtils.removeFolderRecursively(defaultAssetsFolder);
        }
        defaultAssetsFolder.mkdirs();
        File libsFolder = new File("MCServerBanner", "libs");
        if (libsFolder.exists()) {
            FileUtils.removeFolderRecursively(libsFolder);
        }
        libsFolder.mkdirs();
        ResourceDownloadManager resourceDownloadManager = new ResourceDownloadManager(minecraftVersion, defaultAssetsFolder);
        LibraryDownloadManager libraryDownloadManager = new LibraryDownloadManager(libsFolder);
        resourceDownloadManager.downloadResources(new TriConsumer<>() {
            private ResourceDownloadManager.TaskType lastType = null;

            @Override
            public void accept(ResourceDownloadManager.TaskType type, String fileName, Double percentage) {
                if (type != lastType) {
                    switch (type) {
                        case CLIENT_DOWNLOAD:
                            log("Downloading Assets: Downloading client jar... (1 of 4)");
                            break;
                        case EXTRACT:
                            log("Downloading Assets: Extracting client jar... (2 of 4)");
                            break;
                        case DOWNLOAD:
                            log("Downloading Assets: Downloading InteractionChatDiscordSrvAddon files... (3 of 4)");
                            break;
                        case DONE:
                            break;
                    }
                }
                lastType = type;
            }
        });
        log("Downloading libraries: Downloaded libraries... (4 of 4)");
        libraryDownloadManager.downloadLibraries((downloadResult, jarName, percentage) -> { /* Do Nothing */ });
        log("Done!");
    }

    private void includeLibraries() {
        LibraryLoader.loadLibraries(new File("MCServerBanner", "libs"));
    }
    
    private void log(String message) {
        if (verbose) {
            System.out.println(message);
        }
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    @Override
    public void close() {
        if (resourceManager != null) {
            resourceManager.close();
        }
    }
}