
// Fetch is a custom wrapper around the fetch API.
class Fetch {
    /**
     * Define and create custom headers for Fetch requests
     */
    static headers() {
        let headers: Record<string, string> = {
            "Content-Type": "application/json"
        };

        const searchParams = new URLSearchParams(window.location.search);
        if (searchParams.has("auth")) headers["Authorization"] = `Bearer ${searchParams.get("auth")}`;

        return headers;
    }

    /**
     * Uses fetch to make a request to the in-house API
     * @param {string} method Type of request to make
     * @param {string} url endpoint for the API call
     * @param {boolean} showLoader if true displays a loading animation
     */
    static async performRequest(method: string, url: string, showLoader: boolean) {
        let err = null;
        // Display loading animation for the user
        // if (showLoader) Loader.mount();
        let headers = Fetch.headers();

        let result = await fetch(`${url}`, {
            method: method,
            headers: headers
        }).catch((error) => {
            err = error;
        }).finally(() => {
            // if (showLoader) Loader.unmount();
        }) as Response;

        if (err) throw err;
        if (result.status === 204) return {};
        if (result.status < 200 || result.status > 299) throw result;
        return await result.json();
    }

    /**
     * Use fetch's post request
     * @param {string} url endpoint for fetch request
     * @param {Object} data payload of information
     * @param {boolean} showLoader if true displays a loading animation
     */
    static async post(url: string, data: any, showLoader = true) {
        let err = null;
        // if (showLoader) Loader.mount();
        let result = await fetch(`${url}`, {
            method: "POST",
            headers: Fetch.headers(),
            body: JSON.stringify(data)
        }).catch((error) => {
            err = error;
        }).finally(() => {
            // if (showLoader) Loader.unmount();
        }) as Response;

        if (err) throw err;
        if (result.status === 204 || result.status === 201) return {};
        if (result.status < 200 || result.status > 299) throw result;
        return await result.json();
    }

    /**
     * Use fetch's patch request
     * @param {string} url API endpoint to access
     * @param {Object} data Payload to patch with
     * @param {boolean} showLoader if true displays a loading animation
     */
    static async patch(url: string, data: any, showLoader = true) {
        let err = null;
        // if (showLoader) Loader.mount();
        let result = await fetch(`${url}`, {
            method: "PATCH",
            headers: Fetch.headers(),
            body: JSON.stringify(data)
        }).catch((error) => {
            err = error;
        }).finally(() => {
            // if (showLoader) Loader.unmount();
        }) as Response;

        if (err) throw err;
        if (result.status === 204 || result.status === 201) return {};
        if (result.status < 200 || result.status > 299) throw result;
        return await result.json();
    }


    /**
     * Use fetch's put request
     * @param {string} url endpoint for fetch request
     * @param {Object} data payload of information
     * @param {boolean} showLoader if true displays a loading animation
     */
    static async put(url: string, data: any, showLoader = true) {
        let err = null;
        // if (showLoader) Loader.mount();
        let result = await fetch(`${url}`, {
            method: "PUT",
            headers: Fetch.headers(),
            body: JSON.stringify(data)
        }).catch((error) => {
            err = error;
        }).finally(() => {
            // if (showLoader) Loader.unmount();
        }) as Response;

        if (err) throw err;
        if (result.status === 204 || result.status === 201) return {};
        if (result.status < 200 || result.status > 299) throw result;
        return await result.json();
    }

    /**
     * GET request to specific url, used to access in-house API
     * @param {string} url url to perform API request
     * @param {boolean} showLoader boolean value to display loading animation
     */
    static get(url: string, showLoader = true) {
        return Fetch.performRequest("GET", url, showLoader);
    }

    /**
     * DELETE request to specified url
     * @param {string} url url to perform API request
     * @param {boolean} showLoader boolean value to display loading animation
     */
    static delete(url: string, showLoader = true) {
        return Fetch.performRequest("DELETE", url, showLoader);
    }
}

export default Fetch;
