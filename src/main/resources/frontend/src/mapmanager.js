import Fetch from "./fetch";
import Scrim from "./Scrim";

class MapManager {
    #onUpdateListener;

    setOnUpdateListener(listener) {
        this.#onUpdateListener = listener;
    }

    async uploadMaps(maps, category) {
        Scrim.mount();
        try {
            for (let file of maps) {
                let imageContents = await this.getImage(file);
                if (!imageContents) continue;

                await Fetch.post("/maps", {
                    name: file.name.replace(".png", ""),
                    category: category,
                    image: imageContents
                });
                await this.#onUpdateListener();
            }
        } catch (e) {
            throw e;
        } finally {
            Scrim.unmount();
        }
    }

    async deleteMap(id) {
        Scrim.mount();
        try {
            await Fetch.delete(`/maps/${id}`);
            await this.#onUpdateListener();
        } catch (e) {
            throw e;
        } finally {
            Scrim.unmount();
        }
    }

    async deleteCollection(id, collection) {
        Scrim.mount();
        try {
            await Fetch.delete(`/maps/${id}/collection/${encodeURIComponent(collection)}`);
            await this.#onUpdateListener();
        } catch (e) {
            throw e;
        } finally {
            Scrim.unmount();
        }
    }

    async giveMap(id) {
        await Fetch.get(`/maps/${id}/give`)
    }

    async setCatg(id, catg) {
        Scrim.mount();
        try {
            await Fetch.post(`/maps/${id}/category`, {
                category: catg
            });
            await this.#onUpdateListener();
        } catch (e) {
            throw e;
        } finally {
            Scrim.unmount();
        }
    }

    async addToCollection(id, collection) {
        Scrim.mount();
        try {
            await Fetch.post(`/maps/${id}/collection`, {
                name: collection
            });
            await this.#onUpdateListener();
        } catch (e) {
            throw e;
        } finally {
            Scrim.unmount();
        }
    }

    async setName(id, name) {
        Scrim.mount();
        try {
            await Fetch.post(`/maps/${id}/name`, {
                name: name
            });
            await this.#onUpdateListener();
        } catch (e) {
            throw e;
        } finally {
            Scrim.unmount();
        }
    }

    async updateImage(id, file) {
        Scrim.mount();
        try {
            let imageContents = await this.getImage(file);
            if (!imageContents) return;

            await Fetch.put(`/maps/${id}`, {
                image: imageContents
            });
            await this.#onUpdateListener();
        } catch (e) {
            throw e;
        } finally {
            Scrim.unmount();
        }
    }

    async createRotondo(id) {
        Scrim.mount();
        try {
            await Fetch.post(`/maps/${id}/rotondo`, {

            });
            await this.#onUpdateListener();
        } catch (e) {
            throw e;
        } finally {
            Scrim.unmount();
        }
    }

    getImage(file) {
        return new Promise((res, rej) => {
            let reader = new FileReader();
            reader.onload = () => {
                //Perform checks
                let image = new Image();
                image.src = reader.result.toString();
                image.onload = () => {
                    if (!(image.width % 128 === 0 && image.width % 128 === 0)) {
                        if (!window.confirm("You are uploading an image with a non-canon size. For best results, the image should be sized in multiples of 128x128.")) {
                            rej("Cancelled")
                            return;
                        }
                    }
                    res(reader.result.substr(reader.result.indexOf(",") + 1));
                }
            }
            reader.onerror = error => rej(error);
            reader.readAsDataURL(file);
        });
    }
}

export default MapManager;