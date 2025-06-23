package com.azasad.createrecolored.content;

import com.azasad.createrecolored.CreateReColored;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ColoredTags {
    public static <T> TagKey<T> optionalTag(Registry<T> registry,
                                            ResourceLocation id) {
        return TagKey.create(registry.key(), id);
    }

    public static <T> TagKey<T> forgeTag(Registry<T> registry, String path) {
        return optionalTag(registry, ResourceLocation.fromNamespaceAndPath("c", path));
    }

    public enum NameSpace {
        MOD(CreateReColored.MOD_ID, false, true),
        CREATE("create-colored"),
        FORGE("forge"),
        TIC("tic"),
        QUARK("quark");

        public final String id;
        public final boolean optionalDefault;
        public final boolean alwaysDatagenDefault;

        NameSpace(String id) {
            this(id, true, false);
        }

        NameSpace(String id, boolean optionalDefault, boolean alwaysDatagenDefault) {
            this.id = id;
            this.optionalDefault = optionalDefault;
            this.alwaysDatagenDefault = alwaysDatagenDefault;
        }
    }

    public enum ColoredItemTags {
        COLORED_PIPES,
        COLORED_TANKS;

        public final TagKey<Item> tag;
        public final boolean alwaysDatagen;

        ColoredItemTags() {
            this(NameSpace.MOD);
        }

        ColoredItemTags(ColoredTags.NameSpace namespace) {
            this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
        }

        ColoredItemTags(ColoredTags.NameSpace namespace, String path) {
            this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
        }

        ColoredItemTags(ColoredTags.NameSpace namespace, boolean optional, boolean alwaysDatagen) {
            this(namespace, null, optional, alwaysDatagen);
        }

        ColoredItemTags(ColoredTags.NameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, path);
            tag = optionalTag(BuiltInRegistries.ITEM, id);

            this.alwaysDatagen = alwaysDatagen;
        }

        private static void initialize() {}

    }

    public static void initialize() {
        ColoredItemTags.initialize();
    }
}
