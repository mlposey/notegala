'use strict';

module.exports = {
    /** Describes an abstract collection of objects */    
    Repository: class Repository {
        constructor() {
            // A default limit on number of items to retrieve from #find(...)
            this.DEFAULT_LIMIT = 1000;
        }

        add(item) {
            throw new Error('#add not implemented');
        }

        remove(item) {
            throw new Error('#remove not implemented');
        }

        replace(item) {
            throw new Error('#replace not implemented');
        }

        /**
         * @param {Specification} specification
         * @param {number} limit Max items to return
         */
        find(specification, limit) {
            throw new Error('#find not implemented');
        }
    },
    NotFoundError: class NotFoundError extends Error {
        constructor() {
            super('object not found in repository')
        }
    }
};