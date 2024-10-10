import React from "react";
import Fetch from "../fetch";
import MapCategory from "./mapCategory";
import MapManager from "../mapmanager";

import Styles from "./index.module.css";
import Sidebar from "./sidebar";
import MapCollection from "./mapCollection";
import {CategoryContainer} from "./categoryContainer";

class MapView extends React.Component {
    constructor(props) {
        super(props);

        this.mapManager = new MapManager();
        this.mapManager.setOnUpdateListener(this.updateData.bind(this));

        this.state = {
            searchQuery: "",
            show: "local"
        };
    }

    async componentDidMount() {
        await this.updateData();
    }

    async updateData() {
        try {
            this.setState({
                data: await Fetch.get("/maps"),
                collections: await Fetch.get("/collections")
            })
        } catch (e) {
            this.setState({
                error: "load"
            });
        }
    }

    render() {
        if (this.state.error) {
            return <div>
                Please reload
            </div>
        } else if (this.state.data) {
            return <div className={Styles.MainContainer}>
                <Sidebar setShow={(show) => this.setState({show: show})} show={this.state.show} manager={this.mapManager} data={this.state.data} collections={this.state.collections} query={this.state.searchQuery} onSearchUpdated={this.searchUpdated.bind(this)} />
                {this.state.show === "local" &&
                    <div className={Styles.LocalMaps}>
                        {/*<div className={Styles.SectionHeader}>My Maps</div>*/}
                        <CategoryContainer>
                            {["", ...this.state.data.map(map => map.category ? map.category : "").filter(category => category !== "")].filter((category, index, array) => array.indexOf(category) === index).map(category => <MapCategory manager={this.mapManager} category={category} key={category} data={this.state.data} onFilter={this.filter.bind(this)} />)}
                        </CategoryContainer>
                    </div>
                }
                {this.state.show === "global" &&
                    <div className={Styles.GlobalMaps}>
                        {/*<div className={Styles.SectionHeader}>Global Collections</div>*/}
                        <CategoryContainer>
                            {Object.keys(this.state.collections).map(collection => <MapCollection manager={this.mapManager} collection={collection} key={collection} data={this.state.collections[collection]} onFilter={this.filter.bind(this)} />)}
                        </CategoryContainer>
                    </div>
                }
            </div>
        } else {
            return <div>
                Please wait
            </div>
        }
    }

    filter(map) {
        return map.name.toLowerCase().includes(this.state.searchQuery.toLowerCase());
    }

    searchUpdated(query) {
        this.setState({
            searchQuery: query
        });
    }
}

export default MapView;